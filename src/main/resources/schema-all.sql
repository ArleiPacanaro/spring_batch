DROP TABLE IF EXISTS PESSOA;

CREATE TABLE PESSOA(
id          serial,
nome        varchar(240),
endereco    varchar(240),
bairro      varchar(240),
cidade      varchar(240),
estado      varchar(240),
create_date_time timestamp
)