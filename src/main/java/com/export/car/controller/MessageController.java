package com.export.car.controller;

import com.export.car.dto.MessageCreateRequest;
import com.export.car.dto.MessageReplyRequest;
import com.export.car.dto.MessageThreadListResponse;
import com.export.car.dto.MessageThreadResponse;
import com.export.car.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages/threads")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageThreadResponse create(Authentication auth,
                                        @Valid @RequestBody MessageCreateRequest request) {
        return messageService.createThread(auth.getName(), request);
    }

    @GetMapping
    public MessageThreadListResponse list(Authentication auth,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int limit) {
        return messageService.getThreads(auth.getName(), page, limit);
    }

    @GetMapping("/{id}")
    public MessageThreadResponse get(Authentication auth, @PathVariable Long id) {
        return messageService.getThread(auth.getName(), id);
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageThreadResponse reply(Authentication auth,
                                       @PathVariable Long id,
                                       @Valid @RequestBody MessageReplyRequest request) {
        return messageService.reply(auth.getName(), id, request);
    }

    @PatchMapping("/{id}/close")
    public MessageThreadResponse close(Authentication auth, @PathVariable Long id) {
        return messageService.close(auth.getName(), id);
    }
}