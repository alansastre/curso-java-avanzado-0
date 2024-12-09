
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    price DOUBLE,
    quantity INT,
    active BOOLEAN,
    creation_date TIMESTAMP,
    manufacturer_id BIGINT
);