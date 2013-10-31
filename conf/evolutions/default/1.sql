# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actors (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_actors_name unique (name),
  constraint pk_actors primary key (id))
;

create table authTokens (
  id                        bigint not null,
  token                     varchar(255),
  active                    boolean,
  creation_date             timestamp not null,
  constraint uq_authTokens_token unique (token),
  constraint pk_authTokens primary key (id))
;

create table episodes (
  id                        bigint not null,
  season_id                 bigint not null,
  title                     varchar(255),
  number                    integer,
  description               Text,
  airtime                   timestamp,
  constraint uq_episodes_1 unique (number,season_id),
  constraint pk_episodes primary key (id))
;

create table networks (
  id                        bigint not null,
  name                      varchar(255),
  constraint uq_networks_name unique (name),
  constraint pk_networks primary key (id))
;

create table seasons (
  id                        bigint not null,
  show_id                   bigint not null,
  number                    integer,
  constraint uq_seasons_1 unique (number,show_id),
  constraint pk_seasons primary key (id))
;

create table settings (
  hide_descriptions         boolean,
  passed_days_to_show       integer)
;

create table shows (
  id                        bigint not null,
  tvdb_id                   integer,
  title                     varchar(180),
  description               Text,
  airday                    integer,
  airtime                   varchar(255),
  network_id                bigint,
  constraint ck_shows_airday check (airday in (0,1,2,3,4,5,6)),
  constraint uq_shows_tvdb_id unique (tvdb_id),
  constraint uq_shows_title unique (title),
  constraint pk_shows primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(120),
  password                  varchar(255),
  auth_token_id             bigint,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id))
;


create table shows_actors (
  shows_id                       bigint not null,
  actors_id                      bigint not null,
  constraint pk_shows_actors primary key (shows_id, actors_id))
;

create table users_shows (
  users_id                       bigint not null,
  shows_id                       bigint not null,
  constraint pk_users_shows primary key (users_id, shows_id))
;
create sequence actors_seq;

create sequence authTokens_seq;

create sequence episodes_seq;

create sequence networks_seq;

create sequence seasons_seq;

create sequence shows_seq;

create sequence users_seq;

alter table episodes add constraint fk_episodes_seasons_1 foreign key (season_id) references seasons (id) on delete restrict on update restrict;
create index ix_episodes_seasons_1 on episodes (season_id);
alter table seasons add constraint fk_seasons_shows_2 foreign key (show_id) references shows (id) on delete restrict on update restrict;
create index ix_seasons_shows_2 on seasons (show_id);
alter table shows add constraint fk_shows_network_3 foreign key (network_id) references networks (id) on delete restrict on update restrict;
create index ix_shows_network_3 on shows (network_id);
alter table users add constraint fk_users_authToken_4 foreign key (auth_token_id) references authTokens (id) on delete restrict on update restrict;
create index ix_users_authToken_4 on users (auth_token_id);



alter table shows_actors add constraint fk_shows_actors_shows_01 foreign key (shows_id) references shows (id) on delete restrict on update restrict;

alter table shows_actors add constraint fk_shows_actors_actors_02 foreign key (actors_id) references actors (id) on delete restrict on update restrict;

alter table users_shows add constraint fk_users_shows_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_shows add constraint fk_users_shows_shows_02 foreign key (shows_id) references shows (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists actors;

drop table if exists authTokens;

drop table if exists episodes;

drop table if exists networks;

drop table if exists seasons;

drop table if exists settings;

drop table if exists shows;

drop table if exists shows_actors;

drop table if exists users;

drop table if exists users_shows;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists actors_seq;

drop sequence if exists authTokens_seq;

drop sequence if exists episodes_seq;

drop sequence if exists networks_seq;

drop sequence if exists seasons_seq;

drop sequence if exists shows_seq;

drop sequence if exists users_seq;

