package com.rangein.detective.ai_detective.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rangein.detective.ai_detective.dtos.controllers.*;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.dtos.services.AiMessageRequest;
import com.rangein.detective.ai_detective.dtos.services.*;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.dtos.services.GameState;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AiService aiService;

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

    public GameResultDto getGameResult(String gameId) {
        var game = gameRepository.findByIdWithMessageHistory(gameId).orElseThrow();
        return new GameResultDto(game.getFinished(), game.getScore());
    }


    @Transactional
    public ReplyMessageDto sendMessage(SendMessageDto requestDto) {
        var game = gameRepository.findById(requestDto.gameId()).orElseThrow();
        var userMessage = new MessageDto(GameRole.PLAYER.name(), requestDto.message());
        GameState gameState = game.getGameState();
        GameUpdate updatedGame = aiService.talkToAssistant(new AiMessageRequest(gameState, userMessage));
        updateGame(game, updatedGame);
        messageRepository.saveMessageToGame(userMessage.message(),userMessage.sender(),game);
        var savedMessages = saveCharsMessages(updatedGame.m(), game);
        return new ReplyMessageDto(
                savedMessages,
                updatedGame.o(),
                updatedGame.f(),
                null
        );
    }

    // *****************     PRIVATE METHODS     *********************

    private void updateGame(Game game, GameUpdate gameOverview) {
        game.setGameState(gameOverview.g());
        game.setOptions(gameOverview.o());
        Boolean finished = gameOverview.f();
        if (Boolean.TRUE.equals(finished)) {
            game.setFinished(true);
            game.setScore(getGameResult(game, Boolean.TRUE.equals(gameOverview.ok())));
        }
    }

    private Game setupNewGameForUser(User user) throws JsonProcessingException {
        var game = Game.builder().user(user).build();
        GameSetup setup = aiService.setupStory();
        game.setTitle(setup.t());
        game.setStory(setup.p());
        game.setOptions(setup.o());
        game.setGameState(setup.g());
        game = gameRepository.save(game);
        saveCharsMessages(setup.m(), game);
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

    private List<MessageWithIdDto> saveCharsMessages(List<CharacterMessage> messages, Game game) {
        var transformed = messages.stream()
                .map(msg ->
                        Message.builder()
                                .game(game)
                                .message(msg.m())
                                .sender(msg.who())
                                .timestamp(Instant.now())
                                .build()
                ).toList();
        System.out.println(transformed);
        return messageRepository.saveAll(transformed)
                .stream()
                .map(m -> new MessageWithIdDto(m.getMessageId(), m.getSender(), m.getMessage()))
                .toList();
    }

    private Page<MessageWithIdDto> getMessageHistory(String gameId, Pageable pageable) {
        return messageRepository.findPaginatedMessageHistoryByGameId(gameId, pageable);
    }

    private GameResult getGameResult(Game game, Boolean success) {
        if (!success) return GameResult.MISS;
        var gameMessageHistoryLen = game.getMessages().size();
        var GOLD_MAX_MESSAGE_LEN = 20;
        var SILVER_MAX_MESSAGE_LEN = 30;

        if (gameMessageHistoryLen <= GOLD_MAX_MESSAGE_LEN) return GameResult.GOLD;
        if (gameMessageHistoryLen <= SILVER_MAX_MESSAGE_LEN) return GameResult.SILVER;
        return GameResult.BRONZE;
    }
}

