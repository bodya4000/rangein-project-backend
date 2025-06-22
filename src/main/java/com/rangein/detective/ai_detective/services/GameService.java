package com.rangein.detective.ai_detective.services;

import com.google.gson.Gson;
import com.rangein.detective.ai_detective.contants.AiConstants;
import com.rangein.detective.ai_detective.dtos.game.*;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;
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

    @Transactional
    @SneakyThrows
    public SetupResponseDto setup(String userId) {
        var userGames = gameRepository.findGamesByUserId(userId);
        if (!userGames.isEmpty()) {
            var game = userGames.getFirst();
            return new SetupResponseDto(
                    game.getGameId(),
                    new DetectiveStoryDto(game.getTitle(), game.getStory()),
                    getMessageHistory(game)
            );
        }

        var user = userRepository.findById(userId).orElseThrow();
        var game = Game.builder()
                .user(user)
                .build();
        gameRepository.save(game);

        var gameSetupInfo = aiService.setupStory();
        saveInitialMessages(game, gameSetupInfo);
        updateGameFromStory(game, gameSetupInfo.story());

        return new SetupResponseDto(
                game.getGameId(),
                new DetectiveStoryDto(game.getTitle(), game.getStory()),
                getMessageHistory(game)
        );
    }

    public Page<MessageDto> getPaginatedGameMessages(String gameId, Pageable pageable) {
        return messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
    }

    @Transactional
    public MessageDto sendMessage(SendMessageRequestDto requestDto) {
        var game = gameRepository.findByIdWithMessageHistory(requestDto.gameId()).orElseThrow();

        var messageHistoryForAi = messageRepository.findAllMappedToAiMessageDto(requestDto.gameId());
        messageHistoryForAi.add(new AiMessageDto(GameRole.DETECTIVE_USER, requestDto.message()));

        var aiMessageDto = aiService.talkToAssistant(messageHistoryForAi);

        messageRepository.saveMessageToGame(requestDto.message(), GameRole.DETECTIVE_USER, game);
        return messageRepository.saveMessageToGame(aiMessageDto.message(), aiMessageDto.role(), game);
    }

    // *****************     PRIVATE METHODS     *********************

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
