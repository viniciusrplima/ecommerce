ALTER TABLE product_type ADD COLUMN image_key varchar(255);

ALTER TABLE IF EXISTS product_type
    ADD CONSTRAINT product_type_image__image_key_fk FOREIGN KEY (image_key) REFERENCES image;
