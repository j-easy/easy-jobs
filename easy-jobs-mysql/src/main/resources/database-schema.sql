------------------
-- drop all tables
------------------
drop table if exists job_execution;
drop table if exists job_request;
drop table if exists job;
drop table if exists users; -- user is a keyword

------------------
-- create tables
------------------

create table users (
  name varchar(255) primary key,
  password varchar(1024)
);

create table job (
  id bigint primary key,
  name varchar(255)
);

create table job_request (
  id bigint auto_increment primary key,
  job_id bigint not null,
  parameters varchar(4096),
  status varchar(255),
  creation_date datetime,
  processing_date datetime
);

create table job_execution (
  id bigint auto_increment primary key,
  request_id bigint not null,
  status varchar(255),
  job_status varchar(255),
  start_date datetime,
  end_date datetime
);

------------------
-- add constraints
------------------
alter table job_request add foreign key (job_id) references job(id);
alter table job_execution add foreign key (request_id) references job_request(id);
