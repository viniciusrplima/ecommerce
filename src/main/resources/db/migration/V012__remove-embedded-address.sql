CREATE OR REPLACE FUNCTION move_puchase_embedded_address()
RETURNS void AS $$
DECLARE
    purchase_elem RECORD;
    new_address_id INTEGER;
BEGIN
    FOR purchase_elem IN
        SELECT id FROM purchase
    LOOP
        SELECT nextval('address_id_seq') INTO new_address_id;
        UPDATE purchase SET address_id = new_address_id WHERE id = purchase_elem.id;
        INSERT INTO address
        SELECT
            address_id AS id,
            address_cep AS cep,
            address_city AS city,
            address_complement AS complement,
            address_district AS district,
            address_number AS number,
            address_reference_point AS reference_point,
            address_state AS state,
            address_street AS street
        FROM purchase WHERE id = purchase_elem.id;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

ALTER TABLE purchase ADD COLUMN address_id int8;

ALTER TABLE address RENAME COLUMN address_cep TO cep;
ALTER TABLE address RENAME COLUMN address_city TO city;
ALTER TABLE address RENAME COLUMN address_complement TO complement;
ALTER TABLE address RENAME COLUMN address_district TO district;
ALTER TABLE address RENAME COLUMN address_number TO number;
ALTER TABLE address RENAME COLUMN address_reference_point TO reference_point;
ALTER TABLE address RENAME COLUMN address_state TO state;
ALTER TABLE address RENAME COLUMN address_street TO street;

SELECT move_puchase_embedded_address();

ALTER TABLE purchase DROP COLUMN address_cep;
ALTER TABLE purchase DROP COLUMN address_city;
ALTER TABLE purchase DROP COLUMN address_complement;
ALTER TABLE purchase DROP COLUMN address_district;
ALTER TABLE purchase DROP COLUMN address_number;
ALTER TABLE purchase DROP COLUMN address_reference_point;
ALTER TABLE purchase DROP COLUMN address_state;
ALTER TABLE purchase DROP COLUMN address_street;

ALTER TABLE purchase ADD CONSTRAINT purchase_address_fk
    FOREIGN KEY (address_id) REFERENCES address;