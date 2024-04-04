create sequence hibernate_sequence start 1 increment 1;
create table user(id int8 not null, firstName varchar(255), lastName varchar(255), 
email varchar(255), password varchar(60), enabled boolean not null, isUsing2FA boolean not null,
secret varchar(255), primary key (id));
create table role(id int8 not null, name varchar(255), description varchar(255), primary key (id));
create table privilege(id int8 not null, name varchar(255), description varchar(255), primary key (id));
create table password_reset_token(id int8 not null, token varchar(255), expiryDate date);

