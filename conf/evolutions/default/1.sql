# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table actors (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint uq_actors_name unique (name),
  constraint pk_actors primary key (id))
;

create table authTokens (
  id                        bigint auto_increment not null,
  user_id                   bigint not null,
  token                     varchar(255),
  creation_date             datetime not null,
  constraint uq_authTokens_token unique (token),
  constraint pk_authTokens primary key (id))
;

create table episodes (
  id                        bigint auto_increment not null,
  season_id                 bigint not null,
  title                     varchar(255),
  number                    integer,
  description               Text,
  airtime                   datetime,
  constraint uq_episodes_1 unique (number,season_id),
  constraint pk_episodes primary key (id))
;

create table networks (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint uq_networks_name unique (name),
  constraint pk_networks primary key (id))
;

create table seasons (
  id                        bigint auto_increment not null,
  show_id                   bigint not null,
  number                    integer,
  constraint uq_seasons_1 unique (number,show_id),
  constraint pk_seasons primary key (id))
;

create table shows (
  id                        bigint auto_increment not null,
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
  id                        bigint auto_increment not null,
  email                     varchar(120),
  password                  varchar(255),
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
alter table authTokens add constraint fk_authTokens_users_1 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_authTokens_users_1 on authTokens (user_id);
alter table episodes add constraint fk_episodes_seasons_2 foreign key (season_id) references seasons (id) on delete restrict on update restrict;
create index ix_episodes_seasons_2 on episodes (season_id);
alter table seasons add constraint fk_seasons_shows_3 foreign key (show_id) references shows (id) on delete restrict on update restrict;
create index ix_seasons_shows_3 on seasons (show_id);
alter table shows add constraint fk_shows_network_4 foreign key (network_id) references networks (id) on delete restrict on update restrict;
create index ix_shows_network_4 on shows (network_id);



alter table shows_actors add constraint fk_shows_actors_shows_01 foreign key (shows_id) references shows (id) on delete restrict on update restrict;

alter table shows_actors add constraint fk_shows_actors_actors_02 foreign key (actors_id) references actors (id) on delete restrict on update restrict;

alter table users_shows add constraint fk_users_shows_users_01 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_shows add constraint fk_users_shows_shows_02 foreign key (shows_id) references shows (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table actors;

drop table authTokens;

drop table episodes;

drop table networks;

drop table seasons;

drop table shows;

drop table shows_actors;

drop table users;

drop table users_shows;

SET FOREIGN_KEY_CHECKS=1;

