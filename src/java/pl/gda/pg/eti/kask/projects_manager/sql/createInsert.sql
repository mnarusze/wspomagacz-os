DROP TABLE IF EXISTS proj_has_messages;
DROP TABLE IF EXISTS proj_has_users;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS project_message;
DROP TABLE IF EXISTS project_description;
DROP TABLE IF EXISTS publication_types;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id integer unique not null auto_increment, 
    login varchar(50) unique not null,
    last_login datetime,
    first_name varchar(50) not null, 
    last_name varchar(50) not null,
    indeks varchar(15) not null,
    email varchar(100) not null,
    sshkey varchar(1025),
    PRIMARY KEY (id)
);

CREATE TABLE publication_types (
    id integer unique not null,
    type_name varchar(50) not null,
    publication_description text,
    PRIMARY KEY (id)
);

CREATE TABLE project_description (
    id integer unique not null auto_increment,
    proj_full_name varchar(255),
    proj_description text,
    creation_date datetime,
    last_change datetime,
    proj_logo varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE project_message (
    id integer unique not null auto_increment,
    header varchar(255),
    body text,
    msgdate datetime,
    PRIMARY KEY (id)
);

CREATE TABLE projects (
    id integer unique not null auto_increment,
    proj_name varchar(50) unique not null,
    svn_enabled boolean not null,
    git_enabled boolean not null,
    trac_enabled boolean not null,
    redmine_enabled boolean not null,
    pub_type integer not null,
    proj_description integer not null,
    FOREIGN KEY (pub_type) REFERENCES publication_types(id),
    FOREIGN KEY (proj_description) REFERENCES project_description(id),
    PRIMARY KEY (id)
);

CREATE TABLE user_roles (
    id integer unique not null,
    role_name varchar(50) not null,
    role_description text,
    PRIMARY KEY (id)
);

CREATE TABLE proj_has_users (
    projid integer not null,
    userid integer not null,
    rola integer,
    PRIMARY KEY (projid, userid),
    FOREIGN KEY (rola) REFERENCES user_roles(id),
    FOREIGN KEY (projid) REFERENCES projects(id),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE TABLE proj_has_messages (
    projmsgid integer not null,
    msgid integer not null,
    PRIMARY KEY (projmsgid, msgid),
    FOREIGN KEY (projmsgid) REFERENCES projects(id),
    FOREIGN KEY (msgid) REFERENCES project_message(id)
);

INSERT INTO users (login,first_name,last_name,indeks,email) VALUES
    ("12111dd","Dominik","Dziag","121111","deezet@gmail.com"),
    ("121514zm","Mateusz","Zalewski","121514","zalewski.mateusz@gmail.com");

INSERT INTO publication_types (id, type_name, publication_description) VALUES
    (1, "public", "Projekt publiczny"),
    (2, "partialy_public", "Projekt nie w pelni publiczny"),
    (3, "private", "Projekt prywatny"),
    (4, "hidden", "Projekt ukryty");

INSERT INTO user_roles (id, role_name, role_description) VALUES
    (1, "administrator", "Administruje"),
    (2, "developer", "Developuje"),
    (3, "guest", "Gosciu");
    
