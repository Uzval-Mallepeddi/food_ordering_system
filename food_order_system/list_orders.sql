create table list_orders(
order_id int not null, 
item_id int not null, 
quantity int not null, 
foreign key(order_id) references orders(id), 
foreign key(item_id) references food_item(id))