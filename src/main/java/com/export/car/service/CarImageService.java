package com.export.car.service;

import com.export.car.dto.CarResponse;
import com.export.car.entity.Car;
import com.export.car.entity.CarImage;
import com.export.car.repository.CarImageRepository;
import com.export.car.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private static final int MAX_IMAGES = 10;

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public CarResponse.ImageResponse upload(Long carId, MultipartFile file) {
        Car car = findCar(carId);

        if (car.getImages().size() >= MAX_IMAGES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A car can have at most " + MAX_IMAGES + " images");
        }

        String fileName = fileStorageService.store(file);

        CarImage image = new CarImage();
        image.setCar(car);
        image.setFileName(fileName);
        image.setFileUrl("/uploads/" + fileName);
        image.setMain(car.getImages().isEmpty());

        carImageRepository.save(image);
        car.getImages().add(image);

        return new CarResponse.ImageResponse(image.getId(), image.getFileUrl(), image.isMain());
    }

    @Transactional
    public CarResponse setMain(Long carId, Long imageId) {
        Car car = findCar(carId);
        CarImage target = findImage(car, imageId);

        car.getImages().forEach(i -> i.setMain(false));
        target.setMain(true);

        return CarResponse.from(car);
    }

    @Transactional
    public CarResponse delete(Long carId, Long imageId) {
        Car car = findCar(carId);
        CarImage image = findImage(car, imageId);
        boolean wasMain = image.isMain();

        car.getImages().remove(image);
        fileStorageService.delete(image.getFileName());

        if (wasMain) {
            car.getImages().stream().findFirst().ifPresent(i -> i.setMain(true));
        }

        return CarResponse.from(car);
    }

    private Car findCar(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    private CarImage findImage(Car car, Long imageId) {
        return car.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
    }
}