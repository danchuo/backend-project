delete from shop_unit;

insert into shop_unit(id, date, name, parent_id, type) values
    ('069cb8d7-bbdd-47d3-ad8f-82ef4c269df1', '2022-02-01T12:00:00.000Z', 'Товар', null, 1),
    ('1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2', '2022-02-03T12:00:00.000Z', 'Телевизоры', '069cb8d7-bbdd-47d3-ad8f-82ef4c269df1', 1),
    ('d515e43f-f3f6-4471-bb77-6b455017a2d2', '2022-02-02T12:00:00.000Z', 'Смартфоны', '069cb8d7-bbdd-47d3-ad8f-82ef4c269df1', 1);

insert into shop_unit(id, date, name, parent_id, price, type) values
    ('73bc3b36-02d1-4245-ab35-3106c9ee1c65', '2022-02-03T15:00:00.000Z', 'Goldstar 65\" LED UHD LOL Very Smart', '1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2', 69999, 0),
    ('74b81fda-9cdc-4b63-8927-c978afed5cf4', '2022-02-03T12:00:00.000Z', 'Phyllis 50\" LED UHD Smarter', '1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2', 49999, 0),
    ('98883e8f-0507-482f-bce2-2fb306cf6483', '2022-02-03T12:00:00.000Z', 'Samson 70\" LED UHD Smart', '1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2', 32999, 0),
    ('b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4', '2022-02-02T12:00:00.000Z', 'Xomiа Readme 10', 'd515e43f-f3f6-4471-bb77-6b455017a2d2', 59999, 0),
    ('863e1a7a-1304-42ae-943b-179184c077e3', '2022-02-02T12:00:00.000Z', 'jPhone 13', 'd515e43f-f3f6-4471-bb77-6b455017a2d2', 79999, 0);