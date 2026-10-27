package com.export.car.controller;

import com.export.car.dto.CarCreateRequest;
import com.export.car.dto.CarResponse;
import com.export.car.dto.CarUpdateRequest;
import com.export.car.dto.SuccessResponse;
import com.export.car.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cars")
@RequiredArgsConstructor
public class AdminCarController {

    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponse create(@Valid
                              @RequestBody CarCreateRequest request) {
        return carService.create(request);
    }

    @PutMapping("/{id}")
    public  CarResponse update(@PathVariable Long id,
                               @Valid
                               @RequestBody CarUpdateRequest request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Long id) {
        carService.delete(id);
        return new SuccessResponse("Car has been deleted");
    }

}
