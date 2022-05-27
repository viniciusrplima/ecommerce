CREATE TABLE batch (
    id int8 GENERATED BY DEFAULT AS IDENTITY,
    quantity numeric(19, 2) NOT NULL,
    registered_at timestamp NOT NULL DEFAULT current_timestamp,
    product_id int8 NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS product DROP COLUMN IF EXISTS stock;

ALTER TABLE IF EXISTS batch
    ADD CONSTRAINT batch__product_fk
    FOREIGN KEY (product_id)
    REFERENCES product;