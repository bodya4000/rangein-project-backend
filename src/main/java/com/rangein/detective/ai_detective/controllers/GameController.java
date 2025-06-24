package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.controllers.GameResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.ReplyMessageDto;
import com.rangein.detective.ai_detective.dtos.controllers.SendMessageDto;
import com.rangein.detective.ai_detective.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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




    @PostMapping("/messages")
    @SneakyThrows
    public ResponseEntity<ReplyMessageDto> sendMessage(@RequestBody SendMessageDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gameService.sendMessage(requestDto));
    }

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
