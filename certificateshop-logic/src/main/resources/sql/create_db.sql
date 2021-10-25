CREATE TABLE gift_certificate
(
    id                  INTEGER             NOT NULL AUTO_INCREMENT,
    name                VARCHAR(30) UNIQUE  NOT NULL,
    description         VARCHAR(320)        NOT NULL,
    price               FLOAT               NOT NULL,
    duration            INTEGER             NOT NULL,
    create_date         TIMESTAMP           NOT NULL DEFAULT now(),
    last_update_date    TIMESTAMP           NOT NULL DEFAULT now(),
    PRIMARY KEY (id)
);

CREATE TABLE tag
(
    id                  INTEGER  NOT NULL AUTO_INCREMENT,
    name                VARCHAR(30) UNIQUE  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE has_tag
(
    certificateId                  INTEGER  NOT NULL
        REFERENCES gift_certificate (id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    tagId                          INTEGER  NOT NULL
        REFERENCES tag (id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (certificateId, tagId)
);