CREATE table currency
(
    code       TEXT        NOT NULL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO currency (code)
VALUES ('BRL');

CREATE TABLE product
(
    id             uuid PRIMARY KEY,
    name           TEXT           NOT NULL,
    description    TEXT           NOT NULL,
    price_amount   DECIMAL(19, 4) NOT NULL,
    price_currency TEXT           NOT NULL REFERENCES currency (code)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now()
);