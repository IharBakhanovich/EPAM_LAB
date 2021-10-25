INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert1', 'CertOneDescription', 7, 30, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert2', 'CertTwoDescription', 8, 20, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert3', 'CertThreeDescription', 9, 15, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert4', 'CertFourDescription', 10, 30, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert5', 'CertFiveDescription', 15, 25, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert6', 'CertSixDescription', 9, 15, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert7', 'CertSevenDescription', 12, 12, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date) VALUES ('cert8', 'CertEightDescription', 8, 40, '2021-10-12 13:30:29', '2021-10-12 13:30:33');
INSERT INTO tag (name) VALUES ('tag1');
INSERT INTO tag (name) VALUES ('tag2');
INSERT INTO tag (name) VALUES ('tag3');
INSERT INTO tag (name) VALUES ('tag4');
INSERT INTO tag (name) VALUES ('tag5');
INSERT INTO tag (name) VALUES ('tag6');
INSERT INTO tag (name) VALUES ('tag7');
INSERT INTO has_tag (certificateId, tagId) VALUES (1, 1);
INSERT INTO has_tag (certificateId, tagId) VALUES (1, 2);
INSERT INTO has_tag (certificateId, tagId) VALUES (1, 5);
INSERT INTO has_tag (certificateId, tagId) VALUES (2, 2);
INSERT INTO has_tag (certificateId, tagId) VALUES (2, 3);
INSERT INTO has_tag (certificateId, tagId) VALUES (2, 4);
INSERT INTO has_tag (certificateId, tagId) VALUES (3, 3);
INSERT INTO has_tag (certificateId, tagId) VALUES (4, 4);
INSERT INTO has_tag (certificateId, tagId) VALUES (5, 5);
INSERT INTO has_tag (certificateId, tagId) VALUES (6, 6);
INSERT INTO has_tag (certificateId, tagId) VALUES (7, 7);
