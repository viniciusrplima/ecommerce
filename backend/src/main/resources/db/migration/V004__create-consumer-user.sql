CREATE TABLE customer (
    cpf varchar(255) NOT NULL,
    phone varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    PRIMARY KEY (email)
);

CREATE TABLE customer_address (
    customer_email varchar(255) NOT NULL,
    address_id int8 NOT NULL
);
