-- create table users

CREATE TABLE users (
    id UUID NOT NULL PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    email VARCHAR(254) NOT NULL
)
