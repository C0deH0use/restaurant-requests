CREATE SEQUENCE request_menu_item_id_seq START WITH 1000;

ALTER TABLE "request_menu_item"
    DROP CONSTRAINT request_menu_item_pkey;

ALTER TABLE "request_menu_item"
    ADD id INT NOT NULL DEFAULT NEXTVAL('request_menu_item_id_seq');

ALTER TABLE "request_menu_item"
    ADD CONSTRAINT request_menu_item_pk PRIMARY KEY (id);
