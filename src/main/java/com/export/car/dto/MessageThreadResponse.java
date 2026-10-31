package com.export.car.dto;

import com.export.car.entity.MessageThread;
import com.export.car.entity.ThreadStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MessageThreadResponse(
        Long id,
        Long carId,
        String carTitle,
        Long userId,
        String userName,
        String subject,
        ThreadStatus status,
        List<MessageResponse> messages,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record MessageResponse(
            Long id,
            Long senderId,
            String senderName,
            String text,
            boolean read,
            LocalDateTime createdAt
    ) {
    }

    public static MessageThreadResponse from(MessageThread thread, boolean withMessages) {
        List<MessageResponse> messages = withMessages
                ? thread.getMessages().stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getSender().getId(),
                        m.getSender().getFullName(),
                        m.getText(),
                        m.isRead(),
                        m.getCreatedAt()))
                .toList()
                : List.of();

        return new MessageThreadResponse(
                thread.getId(),
                thread.getCar() != null ? thread.getCar().getId() : null,
                thread.getCar() != null ? thread.getCar().getTitle() : null,
                thread.getUser().getId(),
                thread.getUser().getFullName(),
                thread.getSubject(),
                thread.getStatus(),
                messages,
                thread.getCreatedAt(),
                thread.getUpdatedAt()
        );
    }
}
