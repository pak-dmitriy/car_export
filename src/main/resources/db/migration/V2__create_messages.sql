CREATE TABLE message_threads
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    car_id     BIGINT REFERENCES cars (id) ON DELETE SET NULL,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    subject    VARCHAR(150) NOT NULL,
    status     VARCHAR(20)  NOT NULL CHECK (status IN ('OPEN', 'CLOSED')),
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE messages
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    thread_id  BIGINT        NOT NULL REFERENCES message_threads (id) ON DELETE CASCADE,
    sender_id  BIGINT        NOT NULL REFERENCES users (id),
    text       VARCHAR(2000) NOT NULL,
    is_read    BOOLEAN       NOT NULL DEFAULT false,
    created_at TIMESTAMP     NOT NULL DEFAULT now()
);