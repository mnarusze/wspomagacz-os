DROP TABLE IF EXISTS proj_has_read_only_users;
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
    sshkey varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE projects (
    id smallint not null auto_increment,
    proj_name varchar(30) not null,
    owner smallint not null,
    svn_enabled boolean not null,
    git_enabled boolean not null,
    trac_enabled boolean not null,
    redmine_enabled boolean not null,
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

CREATE TABLE proj_has_read_only_users (
    projidro smallint not null,
    useridro smallint not null,
    PRIMARY KEY (projidro, useridro),
    FOREIGN KEY (projidro) REFERENCES projects(id),
    FOREIGN KEY (useridro) REFERENCES users(id)
);

INSERT INTO users (nickname,firstname,lastname,indeks,email) VALUES
    ("maryl","Maciej","Naruszewicz","121514","maciek.naruszewicz@gmail.com"),
    ("dziagu","Dominik","Dziag","121111","deezet@gmail.com"),
    ("zlewak","Mateusz","Zalewski","121514","zalewski.mateusz@gmail.com");