CREATE TABLE IF NOT EXISTS accounts (
id SERIAL PRIMARY KEY,
balance INTEGER NOT NULL,
uuid UUID NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uuid_accounts_index ON accounts (
uuid
);

CREATE TABLE IF NOT EXISTS transactions (
id SERIAL PRIMARY KEY,
delta INTEGER NOT NULL,
senderuuid UUID NOT NULL,
receiveruuid UUID,
CONSTRAINT fk_sender FOREIGN KEY (senderuuid) REFERENCES accounts(uuid),
CONSTRAINT fk_receiver FOREIGN KEY (receiveruuid) REFERENCES accounts(uuid)
);