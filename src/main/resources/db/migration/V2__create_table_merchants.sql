CREATE TABLE merchants(
    merchant_id VARCHAR(100) PRIMARY KEY ,
    secret_key VARCHAR(250),
    balance NUMERIC,
    created_at TIMESTAMP,
    status VARCHAR(50)
);