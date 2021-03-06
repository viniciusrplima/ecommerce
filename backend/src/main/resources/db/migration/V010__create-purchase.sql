CREATE TABLE purchase (
    id int8 GENERATED BY DEFAULT AS IDENTITY,
    address_cep varchar(10) NOT NULL,
    address_city varchar(50) NOT NULL,
    address_complement varchar(30),
    address_district varchar(30) NOT NULL,
    address_number varchar(10) NOT NULL,
    address_reference_point varchar(100),
    address_state varchar(10) NOT NULL,
    address_street varchar(40) NOT NULL,
    created_at timestamp,
    state varchar(20) NOT NULL,
    shipping numeric(19, 2),
    customer_email varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE purchase_item (
    id int8 GENERATED BY DEFAULT AS IDENTITY,
    quantity numeric(19, 2) NOT NULL,
    unit_price numeric(19, 2) NOT NULL,
    product_id int8 NOT NULL,
    purchase_id int8 NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS purchase
    ADD CONSTRAINT purchase__customer_fk
    FOREIGN KEY (customer_email)
    REFERENCES customer
    ON DELETE SET NULL;

ALTER TABLE IF EXISTS purchase_item
    ADD CONSTRAINT purchase_item__product_fk
    FOREIGN KEY (product_id)
    REFERENCES product;

ALTER TABLE IF EXISTS purchase_item
    ADD CONSTRAINT purchase_item__purchase_fk
    FOREIGN KEY (purchase_id)
    REFERENCES purchase
    ON DELETE CASCADE;
