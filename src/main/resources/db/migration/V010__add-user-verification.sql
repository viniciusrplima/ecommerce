CREATE TABLE IF NOT EXISTS user_email_verification (
    user_email varchar(255) NOT NULL,
    code varchar(255) NOT NULL,
    PRIMARY KEY (user_email)
);

ALTER TABLE user_email_verification
    ADD CONSTRAINT user_email_verification__fk FOREIGN KEY (user_email) REFERENCES auth_user;

ALTER TABLE auth_user
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT false;

UPDATE auth_user SET active = true;