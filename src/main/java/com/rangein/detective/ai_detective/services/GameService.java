package com.rangein.detective.ai_detective.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rangein.detective.ai_detective.dtos.game.DetectiveStoryDto;
import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.dtos.game.SendMessageRequestDto;
import com.rangein.detective.ai_detective.dtos.game.SetupResponseDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;
import com.rangein.detective.ai_detective.repositories.GameRepository;
import com.rangein.detective.ai_detective.repositories.game.MessageRepository;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import com.rangein.detective.ai_detective.services.ai.AiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public SetupResponseDto setup(String userId) {
        var userGames = gameRepository.findGamesByUserId(userId);
        if (!userGames.isEmpty()) {
            var game = userGames.getFirst();
            var messageHistory = messageRepository.findPaginatedMessageHistoryByGameId(game.getGameId(), PageRequest.of(0, 20));
            return new SetupResponseDto(
                    game.getGameId(),
                    new DetectiveStoryDto(game.getTitle(), game.getStory()),
                    messageHistory);
        }

        var user = userRepository.findById(userId).orElseThrow();
        var game = Game.builder()
                .user(user)
                .build();
        gameRepository.save(game);

        List<String> setupMessages;
        setupMessages = aiService.requestGeminiSetup().get();
        messageRepository.saveMessageToGame(setupMessages.get(0), GameRole.RULE_MASTER, game);
        messageRepository.saveMessageToGame(setupMessages.get(1), GameRole.STORYTELLER, game);
        messageRepository.saveMessageToGame(setupMessages.get(2), GameRole.ASSISTANT_AI, game);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(setupMessages.get(1));

        String title = jsonNode.get("title").asText();
        JsonNode partsNode = jsonNode.get("parts");

        List<String> partsList = new ArrayList<>();

        if (partsNode != null && partsNode.isArray()) {
            for (JsonNode part : partsNode) {
                partsList.add(part.asText());
            }
        }

        game.setTitle(title);
        game.setStory(partsList);
        var messageHistory = messageRepository.findPaginatedMessageHistoryByGameId(game.getGameId(), PageRequest.of(0, 20));

        return new SetupResponseDto(
                game.getGameId(),
                new DetectiveStoryDto(game.getTitle(), game.getStory()),
                messageHistory);
    }

    public Page<MessageDto> getPaginatedGameMessages(String gameId, Pageable pageable) {
        return messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
    }

    public CompletableFuture<MessageDto> sendMessage(SendMessageRequestDto requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            var game = gameRepository.findByIdWithMessageHistory(requestDto.gameId())
                    .orElseThrow();
            messageRepository.saveMessageToGame(requestDto.message(), GameRole.DETECTIVE, game);
            return game;
        }).thenCompose(game -> {
            List<MessageDto> history = game.getMessageContext();
            String jsonHistory;
            try {
                jsonHistory = objectMapper.writeValueAsString(history);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return aiService.requestGeminiMessage(jsonHistory)
                    .thenApply(aiResponse -> {
                        game.addMessageToHistory(aiResponse, GameRole.ASSISTANT_AI);
                        return messageRepository.saveMessageToGame(aiResponse, GameRole.ASSISTANT_AI, game);
                    });
        });
    }
}
