DROP TABLE IF EXISTS policies;
DROP TABLE IF EXISTS quotes;

CREATE TABLE quotes (
    id VARCHAR(36) PRIMARY KEY,
    pet_name VARCHAR(100) NOT NULL,
    pet_species VARCHAR(20) NOT NULL, -- PERRO, GATO
    pet_breed VARCHAR(100),
    pet_age INT NOT NULL,
    selected_plan VARCHAR(20) NOT NULL, -- BASICO, PREMIUM
    total_amount NUMERIC(10, 2) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE policies (
    id VARCHAR(36) PRIMARY KEY,
    quote_id VARCHAR(36) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(50) NOT NULL,
    owner_email VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVA, INACTIVA
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);