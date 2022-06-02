INSERT INTO product (
    id, 
    name,
    description,
    price,
    stock,
    active
) VALUES (
    :id, 
    ':name', 
    'sem descrição',
    :price, 
    :stock, 
    true
);

INSERT INTO product_product_type (
    product_id, 
    product_type_id
) VALUES (
    :id, 
    :product_type
);