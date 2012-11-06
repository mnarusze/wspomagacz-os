DROP TABLE IF EXISTS proj_has_users;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;



CREATE TABLE users (
    id smallint not null auto_increment, 
    nickname varchar(30) unique not null,
    firstname varchar(30) not null, 
    lastname varchar(30) not null,
    indeks varchar(30) not null,
    email varchar(30) not null,
    PRIMARY KEY (id)
);

CREATE TABLE projects (
    id smallint not null auto_increment,
    proj_name varchar(30) unique not null,
    owner smallint not null,
    svn_enabled boolean not null,
    git_enabled boolean not null,
    trac_enabled boolean not null,
    is_public boolean not null,
    PRIMARY KEY (id),
    FOREIGN KEY (owner) REFERENCES users(id)
);



CREATE TABLE proj_has_users (
    projid smallint not null,
    userid smallint not null,
    PRIMARY KEY (projid, userid),
    FOREIGN KEY (projid) REFERENCES projects(id),
    FOREIGN KEY (userid) REFERENCES users(id)
);

INSERT INTO users (nickname,firstname,lastname,indeks,email) VALUES
    ("maryl","Maciej","Naruszewicz","121514","maciek.naruszewicz@gmail.com"),
    ("dziagu","Dominik","Dziag","121111","deezet@gmail.com"),
    ("zlewak","Mateusz","Zalewski","121514","zalewski.mateusz@gmail.com");

INSERT INTO projects (proj_name,owner,svn_enabled,git_enabled,trac_enabled,is_public) VALUES
    ("inzynierka",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny1",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka1",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny2",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka2",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny3",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka3",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny4",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka4",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny5",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka5",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny6",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka6",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny7",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka7",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny8",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka8",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny9",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka9",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny10",3,TRUE,TRUE,TRUE,FALSE),
    ("inzynierka10",1,TRUE,TRUE,TRUE,TRUE),
    ("projekt_prywatny11",3,TRUE,TRUE,TRUE,FALSE);

INSERT INTO proj_has_users (projid,userid) VALUES
    (1,1),
    (1,2),
    (1,3),
    (2,1),
    (2,2);