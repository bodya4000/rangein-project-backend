package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.constants.Colors;
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
import java.util.*;


@Service
@RequiredArgsConstructor
public class GameService {

    private static final int GOLD_MAX_MESSAGE_LEN = 20;
    private static final int SILVER_MAX_MESSAGE_LEN = 30;

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageValidateService messageValidateService;
    private final AiService aiService;

    @Transactional
    public Optional<GameResponseDto> getGame(String userId) {
        Optional<User> user = userRepository.findAllByIdWithGames(userId);
        if (user.isPresent()) {
            List<Game> games = user.get().getGame();
            if (games.isEmpty()) return  Optional.empty();
            Game game = games.getFirst();
            return Optional.of(new GameResponseDto(
                    game.getGameId(),
                    game.getTitle(),
                    game.getStory(),
                    game.getOptions()
            ));
        }
        return Optional.empty();
    }


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
//        Optional<ReplyMessageDto> replyMessageDtoOptional = messageValidateService.validateMessage(request.message());
//        if (replyMessageDtoOptional.isPresent()) {
//            System.out.println(replyMessageDtoOptional.get());
//            gameRepository.delete(game);
//            return replyMessageDtoOptional.get();
//        }

        MessageDto playerMessage = new MessageDto(game.getUser().getUsername(), request.message());
        boolean shouldKill = game.getGameState().turn() % 2 == 0 && game.getGameState().turn() != 0;
        AiMessageRequest aiRequest = new AiMessageRequest(game.getGameState(), playerMessage, shouldKill);
        GameUpdate update = aiService.talkToAssistant(aiRequest);

        if (Boolean.TRUE.equals(update.g().result().finished())) {
            return handleGameEnd(game, update);
        }

        return handleGameContinue(game, update, playerMessage);
    }

    // =================== PRIVATE ===================

    private ReplyMessageDto handleGameEnd(Game game, GameUpdate update) {
        gameRepository.delete(game);
//        var gameResultScore = calculateResult(update.g());
        var existingDead = game.getDead() != null ? game.getDead() : List.of();

        var justBeenDead = update.g().result().dead()
                .stream()
                .filter(dead -> !existingDead.contains(dead))
                .findFirst();
        return new ReplyMessageDto(
                List.of(),
                List.of(),
                update.g().result().dead(),
                justBeenDead.orElse(null),
                true,
                update.g().result().ok() ? 3 : 0, // gameResultScore
                update.g().result().m()
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

        var justBeenDead = update.g().result().dead()
                .stream()
                .filter(dead -> !existingDead.contains(dead))
                .findFirst();

        if (justBeenDead.isPresent()) {
            List<String> dead = new ArrayList<>(game.getDead() == null ? List.of() : game.getDead());
            dead.add(justBeenDead.get());
            game.setDead(dead);
        }

        return new ReplyMessageDto(
                savedMessages,
                update.o(),
                update.g().result().dead(),
                justBeenDead.orElse(null),
                false,
                null,
                update.g().scene()
        );
    }

    private Game createGame(User user) {
        GameSetup setup = aiService.setupStory();
        var charColors = new ArrayList<>(Colors.gradientCharMessageColors.values());
        Collections.shuffle(charColors);
        Iterator<List<String>> colorIterator = charColors.iterator();

        List<DbCharacter> chars = setup.g().npcs().stream()
                .map(character -> new DbCharacter(
                        character.name(),
                        character.gender(),
                        colorIterator.next()
                ))
                .toList();

        Game game = Game.builder()
                .user(user)
                .title(setup.t())
                .story(setup.p())
                .options(setup.o())
                .gameState(setup.g())
                .chars(chars)
                .dead(setup.g().result().dead())
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

        System.out.println(game.getChars());
        var savedMessages =  messageRepository.saveAll(entities);
        System.out.println(savedMessages);

        return savedMessages.stream()
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
        if (!gameState.result().ok()) return GameResult.MISS;
        int turn = gameState.turn();
        if (turn <= GOLD_MAX_MESSAGE_LEN) return GameResult.GOLD;
        if (turn <= SILVER_MAX_MESSAGE_LEN) return GameResult.SILVER;
        return GameResult.BRONZE;
    }
}
