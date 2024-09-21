-- Migration script for creating MenuItemEntity table with dedicated sequence
CREATE SEQUENCE menu_item_id_seq START WITH 1000;

CREATE TABLE menu_item
(
    id      INT PRIMARY KEY DEFAULT NEXTVAL('menu_item_id_seq'),
    name    VARCHAR(255) NOT NULL,
    price   INT          NOT NULL,
    volume  INT          NOT NULL,
    packing BOOLEAN      NOT NULL
);