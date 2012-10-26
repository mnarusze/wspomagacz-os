DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS proj_has_users;

CREATE TABLE projects (
    proj_name varchar(30) unique not null, 
    gid int(10) unique not null, 
    owner_uid int(10) not null,
    svn_enabled boolean not null,
    git_enabled boolean not null,
    trac_enabled boolean not null,
    is_public boolean not null,
    PRIMARY KEY (proj_name)
);

CREATE TABLE users (
    nickname varchar(30) unique not null,
    uid int(10) unique not null, 
    firstname varchar(30) not null, 
    lastname varchar(30) not null,
    indeks int(10) not null,
    email varchar(30) not null,
    PRIMARY KEY (nickname)
);

CREATE TABLE proj_has_users (
    proj_name varchar(30) not null,
    user_name varchar(30) not null,
    PRIMARY KEY (proj_name,user_name)
);

INSERT INTO users (nickname,uid,firstname,lastname,indeks,email) VALUES
    ("maryl",1000,"Maciej","Naruszewicz",121514,"maciek.naruszewicz@gmail.com"),
    ("dziagu",1001,"Dominik","Dziag",121111,"deezet@gmail.com"),
    ("zlewak",1002,"Mateusz","Zalewski",121514,"zalewski.mateusz@gmail.com");

INSERT INTO projects (proj_name,gid,owner_uid,svn_enabled,git_enabled,trac_enabled,is_public) VALUES
    ("inzynierka",10000,1000,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny",10001,1000,TRUE,TRUE,TRUE,FALSE);

INSERT INTO proj_has_users (proj_name,user_name) VALUES
    ("inzynierka","maryl"),
    ("inzynierka","dziagu"),
    ("inzynierka","zlewak"),
    ("projekt_prywatny","maryl"),
    ("projekt_prywatny","dziagu");