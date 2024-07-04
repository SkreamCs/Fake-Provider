CREATE TABLE transactions(
    id VARCHAR(250) PRIMARY KEY,
    card_number VARCHAR(250),
    currency VARCHAR(50),
    language VARCHAR(50),
    transaction_type VARCHAR(50),
    amount NUMERIC,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    notification_url VARCHAR(250),
    payment_method VARCHAR(100),
    status VARCHAR(100),
    message VARCHAR(250),
    CONSTRAINT fk_card FOREIGN KEY (card_number) REFERENCES cards(card_number),
        CONSTRAINT fk_customer FOREIGN KEY (card_number) REFERENCES customers(card_number)

);