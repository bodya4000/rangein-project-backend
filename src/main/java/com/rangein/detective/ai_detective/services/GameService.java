package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.contants.Colors;
import com.rangein.detective.ai_detective.dtos.base.PageDto;
import com.rangein.detective.ai_detective.dtos.controllers.*;
import com.rangein.detective.ai_detective.dtos.models.game.GameSetup;
import com.rangein.detective.ai_detective.dtos.models.game.GameState;
import com.rangein.detective.ai_detective.dtos.models.game.GameUpdate;
import com.rangein.detective.ai_detective.dtos.models.game.chars.DbCharacter;
import com.rangein.detective.ai_detective.dtos.models.game.messages.AiCharacterMessage;
import com.rangein.detective.ai_detective.dtos.models.game.messages.ResponseMessage;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.dtos.services.ai.AiMessageRequest;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.enums.GameResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


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
                game.getTitle(),
                game.getStory(),
                game.getOptions()
        );
    }

    public PageDto<ResponseMessage> getMessages(String gameId, Pageable pageable) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Page<MessageWithIdDto> page = messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
        List<ResponseMessage> messages = page.getContent()
                .stream()
                .map(m -> {
                    var colors = game.getChars().stream()
                            .filter(c -> c.name().equals(m.sender()))
                            .findFirst()
                            .map(DbCharacter::messageColors)
                            .orElse(List.of("#000000", "#333333"));
                    return new ResponseMessage(
                            m.messageId(),
                            m.sender(),
                            m.message(),
                            colors
                    );
                })
                .toList();
        return new PageDto<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getNumberOfElements(),
                messages
        );
    }

    @Transactional
    public ReplyMessageDto sendMessage(SendMessageDto request) {
        Game game = gameRepository.findByIdWithUser(request.gameId()).orElseThrow();

        MessageDto playerMessage = new MessageDto(game.getUser().getUsername(), request.message());
        AiMessageRequest aiRequest = new AiMessageRequest(game.getGameState(), playerMessage);
        GameUpdate update = aiService.talkToAssistant(aiRequest);

        if (Boolean.TRUE.equals(update.g().res().f())) {
            return handleGameEnd(game, update);
        }

        return handleGameContinue(game, update, playerMessage);
    }

    // =================== PRIVATE ===================

    private ReplyMessageDto handleGameEnd(Game game, GameUpdate update) {
        gameRepository.delete(game);
        var gameResultScore = calculateResult(update.g());
        var existingDead = game.getDead() != null ? game.getDead() : List.of();

        var justBeenDead = update.g().res().dead()
                .stream()
                .filter(dead -> !existingDead.contains(dead))
                .findFirst();
        return new ReplyMessageDto(
                List.of(),
                List.of(),
                update.g().res().dead(),
                justBeenDead.orElse(null),
                true,
                update.g().res().ok() ? 3 : 0, // gameResultScore
                update.g().res().m()
        );
    }

    private ReplyMessageDto handleGameContinue(Game game, GameUpdate update, MessageDto playerMessage) {
        game.setGameState(update.g());
        game.setOptions(update.o());
        messageRepository.saveMessageToGame(playerMessage.message(), playerMessage.sender(), game);
        List<ResponseMessage> savedMessages = saveCharacterMessages(update.m(), game)
                .stream()
                .map(m -> new ResponseMessage(
                        m.messageId(),
                        m.sender(),
                        m.message(),
                        game.getChars().stream()
                                .filter(c -> c.name().equals(m.sender()))
                                .findFirst()
                                .orElseThrow().messageColors()
                ))
                .toList();

        var existingDead = game.getDead() != null ? game.getDead() : List.of();

        var justBeenDead = update.g().res().dead()
                .stream()
                .filter(dead -> !existingDead.contains(dead))
                .findFirst();

        return new ReplyMessageDto(
                savedMessages,
                update.o(),
                update.g().res().dead(),
                justBeenDead.orElse(null),
                false,
                null,
                update.g().scene()
        );
    }

    private Game createGame(User user) {
        GameSetup setup = aiService.setupStory();
        List<DbCharacter> chars = setup.g().chars().stream()
                .map(character -> {
                    var randomCharColor = new Random().nextInt(Colors.gradientCharMessageColors.size());
                    var charColorList = new ArrayList<>(Colors.gradientCharMessageColors.values());
                    var charColor = charColorList.get(randomCharColor);
                    return  new DbCharacter(
                            character.name(),
                            character.gender(),
                            charColor
                            );
                }).toList();


        Game game = Game.builder()
                .user(user)
                .title(setup.t())
                .story(setup.p())
                .options(setup.o())
                .gameState(setup.g())
                .chars(chars)
                .build();

        game = gameRepository.save(game);
        saveCharacterMessages(setup.m(), game);
        return game;
    }


    private List<ResponseMessage> saveCharacterMessages(List<AiCharacterMessage> messages, Game game) {
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
                .map(m -> {
                    var colors = game.getChars().stream()
                            .filter(c -> c.name().equals(m.getSender()))
                            .findFirst()
                            .orElseThrow()
                            .messageColors();

                    return new ResponseMessage(
                            m.getMessageId(),
                            m.getSender(),
                            m.getMessage(),
                            colors
                    );
                })
                .toList();
    }

    private GameResult calculateResult(GameState gameState) {
        if (!gameState.res().ok()) return GameResult.MISS;
        int turn = gameState.turn();
        if (turn <= GOLD_MAX_MESSAGE_LEN) return GameResult.GOLD;
        if (turn <= SILVER_MAX_MESSAGE_LEN) return GameResult.SILVER;
        return GameResult.BRONZE;
    }
}
