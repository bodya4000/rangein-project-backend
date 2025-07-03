package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.controllers.GameResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.GameResultDto;
import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import com.rangein.detective.ai_detective.dtos.controllers.SendMessageDto;
import com.rangein.detective.ai_detective.enums.GameResult;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<GameResponseDto> getUserGame(@PathVariable String userId) {
        System.out.println("Getting game for user:");
        return ResponseEntity.ok(gameService.setup(userId));
    }

    @GetMapping("/result/{gameId}")
    public ResponseEntity<GameResultDto> getGameResult(@PathVariable String gameId) {
        System.out.println("Getting game for user:");
        return ResponseEntity.ok(gameService.getGameResult(gameId));
    }

    @PostMapping("/messages")
    @SneakyThrows
    public ResponseEntity<ReplyMessageDto> sendMessage(@RequestBody SendMessageDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.sendMessage(requestDto));
    }


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


//    @GetMapping("/messages/{gameId}")
//    public ResponseEntity<Page<MessageDto>> getMessages(
//            @PathVariable String gameId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<MessageDto> messages = gameService.getPaginatedGameMessages(gameId, pageable);
//        return ResponseEntity.ok(messages);
//    }

}
