-- Migration script for creating OrderEntity table with dedicated sequence
CREATE SEQUENCE order_id_seq START WITH 1000;

CREATE TABLE "request"
(
    id          INT PRIMARY KEY DEFAULT NEXTVAL('order_id_seq'),
    customer_id INT NOT NULL
);

-- Assuming a join table is necessary for the many-to-many relationship
CREATE TABLE request_menu_item
(
    request_id    INT REFERENCES "request" (id) ON DELETE CASCADE,
    menu_item_id INT REFERENCES "menu_item" (id) ON DELETE CASCADE,
    PRIMARY KEY (request_id, menu_item_id)
);