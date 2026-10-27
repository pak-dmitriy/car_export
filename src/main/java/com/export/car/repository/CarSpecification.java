package com.export.car.repository;

import com.export.car.dto.CarFilter;
import com.export.car.entity.Car;
import com.export.car.entity.CarStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CarSpecification {

    private CarSpecification() {
    }

    public static Specification<Car> build(CarFilter f, Collection<CarStatus> allowedStatuses) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            if (hasText(f.keyword())) {
                String like = "%" + f.keyword().trim().toLowerCase() + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("model")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (hasText(f.brand())) {
                p.add(cb.equal(cb.lower(root.get("brand")), f.brand().trim().toLowerCase()));
            }
            if (hasText(f.model())) {
                p.add(cb.equal(cb.lower(root.get("model")), f.model().trim().toLowerCase()));
            }
            if (f.yearFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("year"), f.yearFrom()));
            }
            if (f.yearTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("year"), f.yearTo()));
            }
            if (f.priceFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("price"), f.priceFrom()));
            }
            if (f.priceTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("price"), f.priceTo()));
            }
            if (hasText(f.fuelType())) {
                p.add(cb.equal(cb.lower(root.get("fuelType")), f.fuelType().trim().toLowerCase()));
            }
            if (hasText(f.transmission())) {
                p.add(cb.equal(cb.lower(root.get("transmission")), f.transmission().trim().toLowerCase()));
            }
            if (hasText(f.bodyType())) {
                p.add(cb.equal(cb.lower(root.get("bodyType")), f.bodyType().trim().toLowerCase()));
            }

            if (f.status() != null) {
                if (allowedStatuses.contains(f.status())) {
                    p.add(cb.equal(root.get("status"), f.status()));
                } else {
                    p.add(cb.disjunction());
                }
            } else {
                p.add(root.get("status").in(allowedStatuses));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}