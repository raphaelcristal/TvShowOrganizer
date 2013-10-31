# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actor (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_actor_name unique (name),
  constraint pk_actor primary key (id))
;

create table auth_token (
  id                        bigint not null,
  token                     varchar(255),
  active                    boolean,
  creation_date             timestamp not null,
  constraint uq_auth_token_token unique (token),
  constraint pk_auth_token primary key (id))
;

create table episode (
  id                        bigint not null,
  season_id                 bigint not null,
  title                     varchar(255),
  number                    integer,
  description               Text,
  airtime                   timestamp,
  constraint uq_episode_1 unique (number,season_id),
  constraint pk_episode primary key (id))
;

create table network (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_network_name unique (name),
  constraint pk_network primary key (id))
;

create table season (
  id                        bigint not null,
  show_id                   bigint not null,
  number                    integer,
  constraint uq_season_1 unique (number,show_id),
  constraint pk_season primary key (id))
;

create table settings (
  hide_descriptions         boolean,
  passed_days_to_show       integer)
;

create table show (
  id                        bigint not null,
  tvdb_id                   integer,
  title                     varchar(180),
  description               Text,
  airday                    integer,
  airtime                   varchar(255),
  network_id                bigint,
  constraint ck_show_airday check (airday in (0,1,2,3,4,5,6)),
  constraint uq_show_tvdb_id unique (tvdb_id),
  constraint uq_show_title unique (title),
  constraint pk_show primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(120),
  password                  varchar(255),
  auth_token_id             bigint,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;


create table show_actor (
  show_id                        bigint not null,
  actor_id                       bigint not null,
  constraint pk_show_actor primary key (show_id, actor_id))
;

create table user_show (
  user_id                        bigint not null,
  show_id                        bigint not null,
  constraint pk_user_show primary key (user_id, show_id))
;
create sequence actor_seq;

create sequence auth_token_seq;

create sequence episode_seq;

create sequence network_seq;

create sequence season_seq;

create sequence show_seq;

create sequence user_seq;

alter table episode add constraint fk_episode_season_1 foreign key (season_id) references season (id) on delete restrict on update restrict;
create index ix_episode_season_1 on episode (season_id);
alter table season add constraint fk_season_show_2 foreign key (show_id) references show (id) on delete restrict on update restrict;
create index ix_season_show_2 on season (show_id);
alter table show add constraint fk_show_network_3 foreign key (network_id) references network (id) on delete restrict on update restrict;
create index ix_show_network_3 on show (network_id);
alter table user add constraint fk_user_authToken_4 foreign key (auth_token_id) references auth_token (id) on delete restrict on update restrict;
create index ix_user_authToken_4 on user (auth_token_id);



alter table show_actor add constraint fk_show_actor_show_01 foreign key (show_id) references show (id) on delete restrict on update restrict;

alter table show_actor add constraint fk_show_actor_actor_02 foreign key (actor_id) references actor (id) on delete restrict on update restrict;

alter table user_show add constraint fk_user_show_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_show add constraint fk_user_show_show_02 foreign key (show_id) references show (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists actor;

drop table if exists auth_token;

drop table if exists episode;

drop table if exists network;

drop table if exists season;

drop table if exists settings;

drop table if exists show;

drop table if exists show_actor;

drop table if exists user;

drop table if exists user_show;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists actor_seq;

drop sequence if exists auth_token_seq;

drop sequence if exists episode_seq;

drop sequence if exists network_seq;

drop sequence if exists season_seq;

drop sequence if exists show_seq;

drop sequence if exists user_seq;

