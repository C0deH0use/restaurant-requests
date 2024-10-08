ALTER TABLE "menu_item"
    ADD COLUMN immediate BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE "request_menu_item"
    ADD COLUMN quantity INT NOT NULL DEFAULT 0;

ALTER TABLE "request_menu_item"
    ADD COLUMN prepared INT NOT NULL DEFAULT 0;

ALTER TABLE "request_menu_item"
    ADD COLUMN immediate BOOLEAN NOT NULL DEFAULT false;