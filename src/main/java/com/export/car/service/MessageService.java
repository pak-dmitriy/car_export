package com.export.car.service;

import com.export.car.dto.MessageCreateRequest;
import com.export.car.dto.MessageReplyRequest;
import com.export.car.dto.MessageThreadListResponse;
import com.export.car.dto.MessageThreadResponse;
import com.export.car.entity.Car;
import com.export.car.entity.CarStatus;
import com.export.car.entity.Message;
import com.export.car.entity.MessageThread;
import com.export.car.entity.Role;
import com.export.car.entity.ThreadStatus;
import com.export.car.entity.User;
import com.export.car.repository.CarRepository;
import com.export.car.repository.MessageThreadRepository;
import com.export.car.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final List<CarStatus> PUBLIC_STATUSES =
            List.of(CarStatus.PUBLISHED, CarStatus.RESERVED);

    private final MessageThreadRepository threadRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Transactional
    public MessageThreadResponse createThread(String email, MessageCreateRequest request) {
        User user = findUser(email);

        MessageThread thread = new MessageThread();
        thread.setUser(user);
        thread.setSubject(request.subject().trim());
        thread.setStatus(ThreadStatus.OPEN);

        if (request.carId() != null) {
            Car car = carRepository.findById(request.carId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
            if (user.getRole() != Role.ADMIN && !PUBLIC_STATUSES.contains(car.getStatus())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
            }
            thread.setCar(car);
        }

        thread.getMessages().add(newMessage(thread, user, request.text()));
        return MessageThreadResponse.from(threadRepository.save(thread), true);
    }

    @Transactional(readOnly = true)
    public MessageThreadListResponse getThreads(String email, int page, int limit) {
        if (page < 1 || limit < 1 || limit > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be >= 1, limit must be between 1 and 50");
        }

        User user = findUser(email);
        Pageable pageable = PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by("id")));

        Page<MessageThread> result = user.getRole() == Role.ADMIN
                ? threadRepository.findAll(pageable)
                : threadRepository.findByUserId(user.getId(), pageable);

        List<MessageThreadResponse> items = result.getContent().stream()
                .map(t -> MessageThreadResponse.from(t, false))
                .toList();
        return new MessageThreadListResponse(items, page, limit, result.getTotalElements());
    }

    @Transactional
    public MessageThreadResponse getThread(String email, Long id) {
        User user = findUser(email);
        MessageThread thread = findAccessible(id, user);

        thread.getMessages().stream()
                .filter(m -> !m.getSender().getId().equals(user.getId()))
                .forEach(m -> m.setRead(true));

        return MessageThreadResponse.from(thread, true);
    }

    @Transactional
    public MessageThreadResponse reply(String email, Long id, MessageReplyRequest request) {
        User user = findUser(email);
        MessageThread thread = findAccessible(id, user);

        if (thread.getStatus() == ThreadStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Thread is closed");
        }

        thread.getMessages().add(newMessage(thread, user, request.text()));
        thread.setUpdatedAt(LocalDateTime.now());
        return MessageThreadResponse.from(threadRepository.saveAndFlush(thread), true);
    }

    @Transactional
    public MessageThreadResponse close(String email, Long id) {
        User user = findUser(email);
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        MessageThread thread = findAccessible(id, user);
        thread.setStatus(ThreadStatus.CLOSED);
        thread.setUpdatedAt(LocalDateTime.now());
        return MessageThreadResponse.from(thread, true);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    private MessageThread findAccessible(Long id, User user) {
        MessageThread thread = threadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread not found"));
        if (user.getRole() != Role.ADMIN && !thread.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return thread;
    }

    private Message newMessage(MessageThread thread, User sender, String text) {
        Message message = new Message();
        message.setThread(thread);
        message.setSender(sender);
        message.setText(text.trim());
        message.setRead(false);
        return message;
    }
}