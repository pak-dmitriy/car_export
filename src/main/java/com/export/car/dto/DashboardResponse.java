package com.export.car.dto;

public record DashboardResponse(
        long totalCars,
        long publishedCars,
        long reservedCars,
        long soldCars,
        long totalUsers,
        long unreadMessages
) {
}