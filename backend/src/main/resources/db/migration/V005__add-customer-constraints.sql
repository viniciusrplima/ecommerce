ALTER TABLE IF EXISTS customer_address
    ADD CONSTRAINT customer_address__address_unique UNIQUE (address_id);

ALTER TABLE IF EXISTS customer
    ADD CONSTRAINT customer__email_fk FOREIGN KEY (email) REFERENCES auth_user;

ALTER TABLE IF EXISTS customer_address
    ADD CONSTRAINT customer_address__address_fk FOREIGN KEY (address_id) REFERENCES
    address;

ALTER TABLE IF EXISTS customer_address
    ADD CONSTRAINT customer_address__customer_fk FOREIGN KEY (customer_email) REFERENCES customer;