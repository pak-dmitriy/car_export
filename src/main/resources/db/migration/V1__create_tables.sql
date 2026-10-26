CREATE TABLE users
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name          VARCHAR(100)                                         NOT NULL,
    email              VARCHAR(255)                                         NOT NULL UNIQUE,
    password_hash      VARCHAR(255)                                         NOT NULL,
    role               VARCHAR(20) CHECK ( role IN ('USER', 'ADMIN'))       NOT NULL,
    status             VARCHAR(20) CHECK ( status IN ('ACTIVE', 'BLOCKED')) NOT NULL,
    preferred_language VARCHAR(10),
    created_at         TIMESTAMP                                            NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP                                            NOT NULL DEFAULT now()
);

CREATE TABLE cars
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year  INT          NOT NULL CHECK ( year >= 1980) ,
    price             NUMERIC(12, 2)                                                           NOT NULL CHECK ( price > 0 ),
    mileage           INT                                                                      NOT NULL CHECK ( mileage >= 0 ),
    fuel_type         VARCHAR(30)                                                              NOT NULL,
    transmission      VARCHAR(30)                                                              NOT NULL,
    engine_volume     NUMERIC(3, 1),
    body_type         VARCHAR(30)                                                              NOT NULL,
    color             VARCHAR(50),
    country_of_origin VARCHAR(100),
    export_location   VARCHAR(100),
    description       VARCHAR(5000),
    status            VARCHAR(50) CHECK (status IN
                                         ('DRAFT', 'PUBLISHED', 'RESERVED', 'SOLD', 'HIDDEN')) NOT NULL,
    created_at        TIMESTAMP                                                                NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP                                                                NOT NULL DEFAULT now()
);

CREATE TABLE car_images
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    car_id     BIGINT       NOT NULL REFERENCES cars (id) ON DELETE CASCADE,
    file_name  VARCHAR(255) NOT NULL,
    file_url   VARCHAR(500) NOT NULL,
    is_main    BOOLEAN      NOT NULL DEFAULT false,
    created_at TIMESTAMP    NOT NULL DEFAULT now()
);