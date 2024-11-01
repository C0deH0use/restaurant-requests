INSERT INTO menu_item(id, name, price, volume, packing, immediate)
VALUES
    (nextval('menu_item_id_seq'), 'hamburger', 999, 1, false, false),
    (nextval('menu_item_id_seq'), 'cheeseburger', 1199, 1, false, false),
    (nextval('menu_item_id_seq'), 'nuggets', 1299, 1, false, false),
    (nextval('menu_item_id_seq'), 'pizza Margarita', 999, 1, false, false),
    (nextval('menu_item_id_seq'), 'pizza Cab', 1399, 1, false, false),
    (nextval('menu_item_id_seq'), 'pizza Polonia', 1699, 1, false, false),
    (nextval('menu_item_id_seq'), 'pizza Italiana', 1499, 1, false, false),


    (nextval('menu_item_id_seq'), 'coke - Small', 399, 1, false, true),
    (nextval('menu_item_id_seq'), 'coke - Large', 850, 1, false, true),

    (nextval('menu_item_id_seq'), 'frise - Small', 450, 1, false, true),
    (nextval('menu_item_id_seq'), 'frise - Medium', 650, 1, false, true)