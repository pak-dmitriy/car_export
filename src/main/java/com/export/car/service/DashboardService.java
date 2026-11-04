package com.export.car.service;

import com.export.car.dto.DashboardResponse;
import com.export.car.entity.CarStatus;
import com.export.car.entity.Role;
import com.export.car.repository.CarRepository;
import com.export.car.repository.MessageRepository;
import com.export.car.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getSummary() {
        return new DashboardResponse(
                carRepository.count(),
                carRepository.countByStatus(CarStatus.PUBLISHED),
                carRepository.countByStatus(CarStatus.RESERVED),
                carRepository.countByStatus(CarStatus.SOLD),
                userRepository.count(),
                messageRepository.countByReadFalseAndSenderRole(Role.USER)
        );
    }
}