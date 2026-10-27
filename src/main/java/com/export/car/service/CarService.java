package com.export.car.service;

import com.export.car.dto.CarCreateRequest;
import com.export.car.dto.CarResponse;
import com.export.car.dto.CarUpdateRequest;
import com.export.car.entity.Car;
import com.export.car.entity.CarStatus;
import com.export.car.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

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
