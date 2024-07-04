CREATE TABLE cards (
    card_number VARCHAR(255) PRIMARY KEY,
    cvv VARCHAR(50) ,
    exp_date VARCHAR(100),
    balance NUMERIC,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(100)
);