INSERT INTO auth_user(
    email, 
    name, 
    password, 
    role
) VALUES (
    ':email', 
    ':name', 
    ':password', 
    ':role'
);