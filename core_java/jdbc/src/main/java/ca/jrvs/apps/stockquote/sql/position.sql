DROP TABLE IF EXISTS position;
CREATE TABLE position (
    symbol                VARCHAR(10) PRIMARY KEY,
    number_of_shares      INT NOT NULL,
    value_paid            DECIMAL(10, 2) NOT NULL,
    CONSTRAINT symbol_fk	FOREIGN KEY (symbol) REFERENCES quote(symbol)
);
