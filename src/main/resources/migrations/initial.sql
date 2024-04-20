create table _user
(
    id       bigint generated by default as identity primary key,
    email    varchar(200)  not null unique,
    password varchar(256)  not null,
    amount   bigint default 1000 not null check (amount >= 0)
);

-- Создание таблицы token
create table token
(
    id           bigint generated by default as identity primary key,
    token_value  varchar(256) not null unique,
    revoked      boolean      not null,
    expired      boolean      not null,
    user_id      bigint       not null references _user(id) on delete cascade
);