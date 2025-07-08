package com.rangein.detective.ai_detective.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rangein.detective.ai_detective.dtos.base.PageDto;
import com.rangein.detective.ai_detective.dtos.controllers.*;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.dtos.services.AiMessageRequest;
import com.rangein.detective.ai_detective.dtos.services.*;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.enums.GameResult;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;
import com.rangein.detective.ai_detective.models.Message;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.GameRepository;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import com.rangein.detective.ai_detective.repositories.game.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;



@Service
@RequiredArgsConstructor
public class GameService {

    private static final int GOLD_MAX_MESSAGE_LEN = 20;
    private static final int SILVER_MAX_MESSAGE_LEN = 30;

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AiService aiService;

    @Transactional
    @SneakyThrows
    public GameResponseDto setup(String userId) {
        User user = userRepository.findAllByIdWithGames(userId).orElseThrow();

        Game game = user.getGame().isEmpty()
                ? createGame(user)
                : user.getGame().getFirst();

        return new GameResponseDto(
                game.getGameId(),
                new DetectiveStoryDto(game.getTitle(), game.getStory()),
                game.getOptions()
        );
    }

    public PageDto<MessageWithIdDto> getMessages(String gameId, Pageable pageable) {
        Page<MessageWithIdDto> page = messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);

        return new PageDto<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getContent()
        );
    }

    @Transactional
    public ReplyMessageDto sendMessage(SendMessageDto request) {
        Game game = gameRepository.findByIdWithUser(request.gameId()).orElseThrow();

        MessageDto playerMessage = new MessageDto(game.getUser().getUsername(), request.message());
        AiMessageRequest aiRequest = new AiMessageRequest(game.getGameState(), playerMessage);
        GameUpdate update = aiService.talkToAssistant(aiRequest);

        if (update != null && Boolean.TRUE.equals(update.f())) {
            return handleGameEnd(game, update);
        }

        return handleGameContinue(game, update, playerMessage);
    }

    // =================== PRIVATE ===================

    private ReplyMessageDto handleGameEnd(Game game, GameUpdate update) {
        gameRepository.delete(game);
        return new ReplyMessageDto(
                List.of(),
                List.of(),
                true,
                update.ok() ? 3 : 0,
                update.g().s()
        );
    }

    private ReplyMessageDto handleGameContinue(Game game, GameUpdate update, MessageDto playerMessage) {
        updateGameState(game, update);
        messageRepository.saveMessageToGame(playerMessage.message(), playerMessage.sender(), game);
        List<MessageWithIdDto> savedMessages = saveCharacterMessages(update.m(), game);
        return new ReplyMessageDto(
                savedMessages,
                update.o(),
                false,
                null,
                update.g().s()
        );
    }

    private Game createGame(User user) throws JsonProcessingException {
        GameSetup setup = aiService.setupStory();

        Game game = Game.builder()
                .user(user)
                .title(setup.t())
                .story(setup.p())
                .options(setup.o())
                .gameState(setup.g())
                .build();

        game = gameRepository.save(game);
        saveCharacterMessages(setup.m(), game);
        return game;
    }

    private void updateGameState(Game game, GameUpdate update) {
        game.setGameState(update.g());
        game.setOptions(update.o());

        if (Boolean.TRUE.equals(update.f())) {
            game.setFinished(true);
            game.setScore(calculateResult(game, Boolean.TRUE.equals(update.ok())));
        }
    }

    private List<MessageWithIdDto> saveCharacterMessages(List<CharacterMessage> messages, Game game) {
        if (messages == null || messages.isEmpty()) return List.of();

        List<Message> entities = messages.stream()
                .map(msg -> Message.builder()
                        .game(game)
                        .message(msg.m())
                        .sender(msg.who())
                        .timestamp(Instant.now())
                        .build())
                .toList();

        return messageRepository.saveAll(entities).stream()
                .map(m -> new MessageWithIdDto(m.getMessageId(), m.getSender(), m.getMessage()))
                .toList();
    }

    private GameResult calculateResult(Game game, boolean success) {
        if (!success) return GameResult.MISS;

        int count = game.getMessages().size();

        if (count <= GOLD_MAX_MESSAGE_LEN) return GameResult.GOLD;
        if (count <= SILVER_MAX_MESSAGE_LEN) return GameResult.SILVER;
        return GameResult.BRONZE;
    }
}
