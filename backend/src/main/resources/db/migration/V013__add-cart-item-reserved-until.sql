ALTER TABLE cart_item ADD COLUMN reserved_until timestamp NOT NULL DEFAULT current_timestamp;
ALTER TABLE cart_item ADD COLUMN reserved boolean NOT NULL DEFAULT FALSE;