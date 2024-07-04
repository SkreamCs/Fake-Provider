CREATE TABLE cards (
                       card_number VARCHAR(255) PRIMARY KEY,
                       cvv VARCHAR(50) ,
                       exp_date VARCHAR(100),
                       balance NUMERIC,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       status VARCHAR(100)
);
CREATE TABLE merchants(
                            merchant_id VARCHAR(100) PRIMARY KEY ,
                            secret_key VARCHAR(250),
                            balance NUMERIC,
                            created_at TIMESTAMP,
                            status VARCHAR(50)
  );
CREATE TABLE customers(
                          card_number VARCHAR(250) PRIMARY KEY,
                          first_name VARCHAR(100),
                          last_name VARCHAR(100),
                          country VARCHAR(50)
);
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
INSERT INTO merchants (merchant_id, secret_key, balance, created_at, status)
VALUES ('1234', '$2y$12$ziLP8UvoQ9TFjolGQfjY1.Jty.XWmfnTOjgViHkrgKAz16qw5vAgO', 100000.00, NOW(), 'ACTIVE');
INSERT INTO cards (card_number, cvv, exp_date, balance, created_at, updated_at, status)
VALUES ('1111111111111111', '123', '12/23', 10000000.00, NOW(), NOW(), 'ACTIVE');