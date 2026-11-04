package com.export.car.repository;

import com.export.car.entity.Message;
import com.export.car.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    long countByReadFalseAndSenderRole(Role role);
}