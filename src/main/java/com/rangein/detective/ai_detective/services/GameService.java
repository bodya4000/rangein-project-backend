package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.game.*;
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

import java.util.List;


@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AiService aiService;
    private final Gson gson;

    @SneakyThrows
    public SetupResponseDto getGameInfo(String gameId) {
        var game = gameRepository.findByIdWithMessageHistory(gameId).orElseThrow();
        return buildSetupResponse(game);
    }

    @Transactional
    @SneakyThrows
    public SetupResponseDto setup(String userId) {
        User user = userRepository.findAllByIdWithGames(userId).orElseThrow();

        Game game = user.getGame().isEmpty() ? createNewGameForUser(user) : user.getGame().getFirst();
        return buildSetupResponse(game);
    }

    public Page<MessageDto> getPaginatedGameMessages(String gameId, Pageable pageable) {
        return messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
    }

    @Transactional
    public SendMessageResponseDto sendMessage(SendMessageRequestDto requestDto) {
        var game = gameRepository.findByIdWithMessageHistory(requestDto.gameId()).orElseThrow();

        var messageHistoryForAi = messageRepository.findAllMappedToAiMessageDto(requestDto.gameId());
        messageHistoryForAi.add(new AiMessageDto(GameRole.DETECTIVE_USER, requestDto.message(), List.of()));

        var aiMessageDto = aiService.talkToAssistant(messageHistoryForAi);

        messageRepository.saveMessageToGame(requestDto.message(), GameRole.DETECTIVE_USER, game);
        var message = messageRepository.saveMessageToGame(aiMessageDto.message(), aiMessageDto.role(), game);
        return new SendMessageResponseDto(
                message.messageId(), message.role(), message.message(), List.of()
        );
    }

    // *****************     PRIVATE METHODS     *********************

    private Game createNewGameForUser(User user) {
        var game = Game.builder()
                .user(user)
                .build();
        gameRepository.save(game);

        var gameSetupInfo = aiService.setupStory();
        saveInitialMessages(game, gameSetupInfo);
        updateGameFromStory(game, gameSetupInfo.story());

        return game;
    }

    private SetupResponseDto buildSetupResponse(Game game) {
        var messagePage = getMessageHistory(game);
        return new SetupResponseDto(
                game.getGameId(),
                new DetectiveStoryDto(game.getTitle(), game.getStory()),
                buildMessageData(messagePage)
        );
    }

    private MessageData buildMessageData(Page<MessageDto> page) {
        return new MessageData(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements()
        );
    }

    private void saveInitialMessages(Game game, GameSetupInfoDto setupInfo) {
        messageRepository.saveMessageToGame(AiConstants.START_PROMPT, GameRole.SYSTEM_BACKEND, game);
        messageRepository.saveMessageToGame(gson.toJson(setupInfo.story()), GameRole.STORYTELLER_AI, game);
        messageRepository.saveMessageToGame(setupInfo.introMessage().message(), GameRole.DETECTIVE_ASSISTANT_AI, game);
    }

    private void updateGameFromStory(Game game, DetectiveStoryDto story) {
        game.setTitle(story.title());
        game.setStory(story.parts());
    }

    private Page<MessageDto> getMessageHistory(Game game) {
        return messageRepository.findPaginatedMessageHistoryByGameId(game.getGameId(), PageRequest.of(0, 20));
    }
}

