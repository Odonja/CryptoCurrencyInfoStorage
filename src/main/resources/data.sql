DROP TABLE IF EXISTS currencies;

CREATE TABLE currencies (
                           ticker VARCHAR(3) PRIMARY KEY NOT NULL,
                           name VARCHAR(250) NOT NULL,
                           number_of_coins long NOT NULL,
                           market_cap long NOT NULL
);

INSERT INTO currencies (ticker, name, number_of_coins, market_cap) VALUES
('BTC', 'Bitcoin', 16770000, 189580000000),
('ETH', 'Ethereum', 96710000, 69280000000),
('XRP', 'Ripple', 38590000000, 64750000000),
('BCH', 'BitcoinCash', 16670000, 69020000000);