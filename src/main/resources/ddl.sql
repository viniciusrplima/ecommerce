create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
create table auth_user (email varchar(255) not null, name varchar(255), password varchar(255), role varchar(255), primary key (email));
create table product (id int8 generated by default as identity, active boolean, description varchar(255), name varchar(255), image oid, price numeric(19, 2), stock numeric(19, 2), primary key (id));
create table product_product_type (product_id int8 not null, product_type_id int8 not null);
create table product_type (id int8 generated by default as identity, description varchar(255), image oid, name varchar(255), primary key (id));
alter table if exists product_product_type add constraint FK3n22y1fv816fo5iomi29gcfni foreign key (product_type_id) references product_type;
alter table if exists product_product_type add constraint FKpkt86314r6ds40xsp34ixr1tv foreign key (product_id) references product;
