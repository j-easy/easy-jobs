------------------
-- drop all tables
------------------
drop table if exists ej_job_execution;
drop table if exists ej_job_execution_request;
drop table if exists ej_job;
drop table if exists ej_user;

------------------
-- create tables
------------------

create table ej_user (
  name varchar(255) primary key,
  password varchar(1024)
);

create table ej_job (
  id bigint primary key,
  name varchar(255),
  description varchar(4096)
);

create table ej_job_execution_request (
  id bigint auto_increment primary key,
  job_id bigint not null,
  parameters varchar(4096),
  status varchar(255),
  creation_date datetime,
  processing_date datetime
);

create table ej_job_execution (
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
alter table ej_job_execution_request add foreign key (job_id) references ej_job(id);
alter table ej_job_execution add foreign key (request_id) references ej_job_execution_request(id);
