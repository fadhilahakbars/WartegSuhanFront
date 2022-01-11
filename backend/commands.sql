-- PEMBUATAN TABLE
create type user_type as enum('ADMIN', 'CASHIER');
create type jenis_menu as enum ('Makanan', 'Minuman', 'Paket');
create table users (username text primary key, password text not null, type user_type default 'CASHIER');
create table menu (id_menu serial primary key, jenis_menu jenis_menu, nama_menu varchar(255), jumlah_stok int, harga bigint);
-- transaksi
create table penjualan (id_order serial, id_menu int not null, jumlah_stok int, date date, constraint pk_menu primary key(id_order, id_menu), constraint fk_menu foreign key(id_menu) references menu(id_menu) on update cascade);

-- PEMBUATAN FUNCTION UNTUK MELAKUKAN PENGURANGAN JUMLAH STOK
CREATE OR REPLACE FUNCTION update_stock()
    RETURNS TRIGGER AS
$$
BEGIN
    EXECUTE format ('UPDATE menu SET jumlah_stok = jumlah_stok - $1 WHERE id_menu = $2') USING new.jumlah_stok, new.id_menu;
    return new;
END
$$
    LANGUAGE plpgsql VOLATILE;


--PEMBUATAN TRIGGER FUNCTION TRANSAKSI
CREATE TRIGGER trigger_update_stok
AFTER INSERT ON penjualan
FOR EACH ROW EXECUTE PROCEDURE update_stock();

insert into users values('admin', 'admin', 'ADMIN');
--MEMASUKKAN KE TABLE MENU
insert into menu (jenis_menu, nama_menu, jumlah_stok, harga) values
('Makanan','Nasi Goreng', 3, 10000), -- 1
('Makanan','Ayam Goreng', 4, 12000),
('Makanan','Sayur Mayur', 2, 5000),
('Makanan','Tempe', 7, 2500),
('Makanan','Tahu', 8, 2500),
('Makanan','Telor', 7, 2000),
('Minuman','Extra Joss', 12, 3000),
('Minuman','Teh Manis', 15, 1500),
('Minuman','Es Jeruk', 11, 4000),
('Makanan','Indomie Goreng', 9, 5000),
('Makanan','Indomie kuah', 10, 5000), -- 1
('Makanan','Steak ayam', 11, 15000),
('Makanan','Kentang goreng', 7, 10000),
('Paket','Paket sederhana 1', 5, 13000),
('Paket','Paket sederhana 2', 5, 14000);