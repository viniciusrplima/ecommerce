CREATE TABLE image (
    image_key varchar(255) NOT NULL,
    bucket_name varchar(255) NOT NULL,
    public_url varchar(255) NOT NULL,
    PRIMARY KEY (image_key)
);

ALTER TABLE product ADD COLUMN image_key varchar(255);

ALTER TABLE IF EXISTS product
    ADD CONSTRAINT product_image__image_key_fk FOREIGN KEY (image_key) REFERENCES image;
