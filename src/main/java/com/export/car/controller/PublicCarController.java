package com.export.car.controller;

import com.export.car.dto.CarFilter;
import com.export.car.dto.CarListResponse;
import com.export.car.dto.CarResponse;
import com.export.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class PublicCarController {

    private final CarService carService;

    @GetMapping
    public CarListResponse list(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "12") int limit,
                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                @RequestParam(defaultValue = "desc") String direction,
                                CarFilter filter) {
        return carService.getPublicCars(filter, page, limit, sortBy, direction);
    }

    @GetMapping("/{id}")
    public CarResponse get(@PathVariable Long id) {
        return carService.getPublicCar(id);
    }
}
