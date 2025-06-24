package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.controllers.GameResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.MessagePageDto;
import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequestDto;
import com.rangein.detective.ai_detective.dtos.controllers.SendMessageDto;
import com.rangein.detective.ai_detective.dtos.services.*;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.GameRepository;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import com.rangein.detective.ai_detective.repositories.game.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AiService aiService;
    private final Gson gson;

    private static final int DEFAULT_PAGE_SIZE = 20;


    @Transactional
    @SneakyThrows
    public GameResponseDto setup(String userId) {
        User user = userRepository.findAllByIdWithGames(userId).orElseThrow();
        Game game = user.getGame().isEmpty() ? setupNewGameForUser(user) : user.getGame().getFirst();
        return buildSetupResponse(game);
    }

    public Page<MessageWithIdDto> getPaginatedGameMessages(String gameId, Pageable pageable) {
        return getMessageHistory(gameId, pageable);
    }

    @Transactional
    public ReplyMessageDto sendMessage(SendMessageDto requestDto) {
        var game = gameRepository.findByIdWithMessageHistory(requestDto.gameId()).orElseThrow();

        var messageHistoryForAi = messageRepository.findAllMessages(requestDto.gameId());
        var userMessage = new MessageDto(GameRole.DETECTIVE_USER, requestDto.message());
        var aiMessageDto = aiService.talkToAssistant(new AiMessageRequestDto(messageHistoryForAi, userMessage));

        messageRepository.saveMessageToGame(requestDto.message(), GameRole.DETECTIVE_USER, game);
        var message = messageRepository.saveMessageToGame(aiMessageDto.message(), aiMessageDto.role(), game);
        return new ReplyMessageDto(
                message.message(),
                message.role(),
                message.message(),
                aiMessageDto.options()
        );
    }

    // *****************     PRIVATE METHODS     *********************

    private Game setupNewGameForUser(User user) {
        var game = Game.builder().user(user).build();
        gameRepository.save(game);

        var gameSetupInfo = aiService.setupStory();
        saveInitialMessages(game, gameSetupInfo);
        updateGameStory(game, gameSetupInfo.story());
        game.setOptions(gameSetupInfo.options());
        return game;
    }

    private GameResponseDto buildSetupResponse(Game game) {
        var messagePage = getMessageHistory(game.getGameId(), PageRequest.of(0, DEFAULT_PAGE_SIZE));
        return new GameResponseDto(
                game.getGameId(),
                new DetectiveStoryDto(game.getTitle(), game.getStory()),
                buildMessageData(messagePage),
                game.getOptions()
        );
    }

    private MessagePageDto buildMessageData(Page<MessageWithIdDto> page) {
        return new MessagePageDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getContent()
                );
    }

    private void saveInitialMessages(Game game, GameSetupInfoDto setupInfo) {
        messageRepository.saveMessageToGame(AiConstants.START_PROMPT, GameRole.SYSTEM_BACKEND, game);
        messageRepository.saveMessageToGame(gson.toJson(setupInfo.story()), GameRole.STORYTELLER_AI, game);
        messageRepository.saveMessageToGame(setupInfo.assistantIntro(), GameRole.DETECTIVE_ASSISTANT_AI, game);
    }

    private void updateGameStory(Game game, DetectiveStoryDto story) {
        game.setTitle(story.title());
        game.setStory(story.parts());
    }

    private Page<MessageWithIdDto> getMessageHistory(String gameId, Pageable pageable) {
        return messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
    }
}

