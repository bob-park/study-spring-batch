create table customer
(
    id        mediumint(8) unsigned NOT NULL auto_increment,
    firstName varchar(255) default null,
    lastName  varchar(255) default null,
    birthDate varchar(255),
    PRIMARY KEY (id)
) auto_increment = 1;