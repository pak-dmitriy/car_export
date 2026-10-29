package com.export.car.controller;

import com.export.car.dto.*;
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
    public CarResponse update(@PathVariable Long id,
                              @Valid
                              @RequestBody CarUpdateRequest request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Long id) {
        carService.delete(id);
        return new SuccessResponse("Car has been deleted");
    }

    @GetMapping
    public CarListResponse list(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "12") int limit,
                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                @RequestParam(defaultValue = "desc") String direction,
                                CarFilter filter) {
        return carService.getAdminCars(filter, page, limit, sortBy, direction);
    }

    @GetMapping("/{id}")
    public CarResponse get(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PatchMapping("/{id}/status")
    public CarResponse changeStatus(@PathVariable Long id,
                                    @Valid
                                    @RequestBody CarStatusRequest request) {
        return carService.changeStatus(id, request.status());
    }

}
