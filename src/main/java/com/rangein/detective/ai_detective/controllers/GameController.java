package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.base.PageDto;
import com.rangein.detective.ai_detective.dtos.controllers.GameResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import com.rangein.detective.ai_detective.dtos.controllers.SendMessageDto;
import com.rangein.detective.ai_detective.dtos.models.game.messages.ResponseMessage;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<GameResponseDto> getUserGame(@PathVariable String userId) {
        var game = gameService.getGame(userId);
        return game
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build());
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<GameResponseDto> createGame(@PathVariable String userId) {
        return ResponseEntity.ok(gameService.setup(userId));
    }

    @PostMapping("/messages")
    public ResponseEntity<ReplyMessageDto> sendMessage(@RequestBody SendMessageDto requestDto) throws ValidationException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.sendMessage(requestDto));
    }

    @GetMapping("/messages/{gameId}")
    public ResponseEntity<PageDto<ResponseMessage>> getMessages(
            @PathVariable String gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageDto<ResponseMessage> messages = gameService.getMessages(gameId, pageable);
        return ResponseEntity.ok(messages);
    }


    //    @GetMapping("/result/{gameId}")
//    public ResponseEntity<GameResultDto> getGameResult(@PathVariable String gameId) {
//        System.out.println("Getting game for user:");
//        return ResponseEntity.ok(gameService.getResult(gameId));
//    }

//    @PostMapping("/messages")
//    @SneakyThrows
//    public ResponseEntity<ReplyMessageDto> sendMessage(@RequestBody SendMessageDto requestDto) {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(new ReplyMessageDto(
//                "msg-12345",
//                GameRole.DETECTIVE_ASSISTANT_AI,
//                "Бінго",
//                List.of("Оглянути тіло", "Допитати охоронця", "Повернутись до відділку"),
//                true,
//                GameResult.GOLD.ordinal()
//        ));
//    }

//    @PostMapping("/{gameId}")
//    public ResponseEntity<SetupResponseDto> getGameInfo(@PathVariable String gameId) {
//        System.out.println("Setting up game for user:");
//        return ResponseEntity.ok(gameService.getGameInfo(gameId));
//    }



}
