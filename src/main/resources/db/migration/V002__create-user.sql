CREATE TABLE auth_user (
    email varchar(255) NOT NULL,
    name varchar(50),
    password varchar(255) NOT NULL,
    role varchar(50) NOT NULL,
    PRIMARY KEY (email)
);
