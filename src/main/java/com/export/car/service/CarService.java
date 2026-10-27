package com.export.car.service;

import com.export.car.dto.*;
import com.export.car.entity.Car;
import com.export.car.entity.CarStatus;
import com.export.car.repository.CarRepository;
import com.export.car.repository.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private static final List<CarStatus> PUBLIC_STATUSES =
            List.of(CarStatus.PUBLISHED, CarStatus.RESERVED);

    private static final Set<String> SORT_FIELDS =
            Set.of("createdAt", "price", "mileage");

    @Transactional(readOnly = true)
    public CarListResponse getPublicCars(CarFilter filter, int page, int limit,
                                         String sortBy, String direction) {
        if (page < 1 || limit < 1 || limit > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "page and limit should be between 1 and 50.");
        }

        if (!SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "sortBy must be one of: " + SORT_FIELDS);
        }

        Sort.Direction dir;
        if ("asc".equalsIgnoreCase(direction)) {
            dir = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            dir = Sort.Direction.DESC;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(dir, sortBy).and(Sort.by("id")));
        Page<Car> result = carRepository.findAll(CarSpecification.build(filter, PUBLIC_STATUSES), pageable);

        List<CarResponse> items = result.getContent().stream().map(CarResponse::from).toList();
        return new CarListResponse(items, page, limit, result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public CarResponse getPublicCar(Long id) {
        Car car = findCar(id);
        if (!PUBLIC_STATUSES.contains(car.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }
        return CarResponse.from(car);
    }

    @Transactional
    public CarResponse create(CarCreateRequest r) {
        validateYear(r.year());

        Car car = new Car();
        car.setTitle(r.title());
        car.setBrand(r.brand());
        car.setModel(r.model());
        car.setYear(r.year());
        car.setPrice(r.price());
        car.setMileage(r.mileage());
        car.setFuelType(r.fuelType());
        car.setTransmission(r.transmission());
        car.setEngineVolume(r.engineVolume());
        car.setBodyType(r.bodyType());
        car.setColor(r.color());
        car.setCountryOfOrigin(r.countryOfOrigin());
        car.setExportLocation(r.exportLocation());
        car.setDescription(r.description());
        car.setStatus(CarStatus.DRAFT);

        return CarResponse.from(carRepository.save(car));
    }

    @Transactional
    public CarResponse update(Long id, CarUpdateRequest r) {
        validateYear(r.year());

        Car car = findCar(id);
        car.setTitle(r.title());
        car.setBrand(r.brand());
        car.setModel(r.model());
        car.setYear(r.year());
        car.setPrice(r.price());
        car.setMileage(r.mileage());
        car.setFuelType(r.fuelType());
        car.setTransmission(r.transmission());
        car.setEngineVolume(r.engineVolume());
        car.setBodyType(r.bodyType());
        car.setColor(r.color());
        car.setCountryOfOrigin(r.countryOfOrigin());
        car.setExportLocation(r.exportLocation());
        car.setDescription(r.description());

        return CarResponse.from(car);
    }

    @Transactional
    public void delete(Long id) {
        carRepository.delete(findCar(id));
    }

    private Car findCar(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Car not found"));
    }

    private void validateYear(int year) {
        int maxYear = Year.now().getValue() + 1;
        if (year > maxYear) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Year must be between 1980 and " + maxYear);
        }
    }
}
