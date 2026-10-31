package com.export.car.repository;

import com.export.car.entity.MessageThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    Page<MessageThread> findByUserId(Long userId, Pageable pageable);
}