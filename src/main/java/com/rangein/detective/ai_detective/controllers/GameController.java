package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.dtos.game.SendMessageRequestDto;
import com.rangein.detective.ai_detective.dtos.game.SendMessageResponseDto;
import com.rangein.detective.ai_detective.dtos.game.SetupResponseDto;
import com.rangein.detective.ai_detective.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<SetupResponseDto> setupGame(@PathVariable String userId) {
        System.out.println("Getting game for user:");
        return ResponseEntity.ok(gameService.setup(userId));
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<SetupResponseDto> getGameInfo(@PathVariable String gameId) {
        System.out.println("Setting up game for user:");
        return ResponseEntity.ok(gameService.getGameInfo(gameId));
    }


    @PostMapping("/messages")
    @SneakyThrows
    public ResponseEntity<SendMessageResponseDto> sendMessage(@RequestBody SendMessageRequestDto requestDto) {
        return ResponseEntity.ok(gameService.sendMessage(requestDto));
    }


    @GetMapping("/messages/{gameId}")
    public ResponseEntity<Page<MessageDto>> getMessages(
            @PathVariable String gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageDto> messages = gameService.getPaginatedGameMessages(gameId, pageable);
        return ResponseEntity.ok(messages);
    }

}
