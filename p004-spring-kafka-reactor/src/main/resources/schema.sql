CREATE TABLE account (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         owner VARCHAR(255) NOT NULL,
                         balance DOUBLE
);

CREATE TABLE transaction (
                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DOUBLE,
    type VARCHAR(255) not null,
    timestamp TIMESTAMP,
    account_id BIGINT,
    FOREIGN KEY (account_id) REFERENCES account(id)


);
