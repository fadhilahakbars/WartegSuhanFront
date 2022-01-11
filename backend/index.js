const express = require('express');
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
const { Client } = require('pg');
const { response } = require('express');
const port = process.env.PORT || 3000;
const client = new Client({
    // Lengkapi koneksi dengan database
    host: "localhost",
    port: 5432,
    user: "postgres",
    password: "fadil123",
    database: "postgres",
});

client.connect((err) =>{
    if (err) {
        console.error(err);
        return;
    }
    console.log('Database Connected');
});

app.get('/user', (req, res) => {
    const query = `SELECT username, password, type FROM users WHERE username = '${req.query.user}' AND password = '${req.query.pass}'`;
    client.query(query, (err, results) => {
        if (err) {
            console.log(err);
            res.statusCode = 500;
            res.send(null);
        } else if (results.rows.length === 0) {
            res.statusCode = 404;
            res.send(`Please check your username or password!`);
        } else {
            res.statusCode = 200;
            res.send(String(results.rows[0].type));
        }
    });
});

//List Stok
app.get('/menu',(req, res) => {
    const query = `SELECT * FROM menu ORDER BY id_menu asc`;
    client.query(query , (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }
        res.send(results.rows);
    });
});

app.post('/menu', function(req, res) {
    const query = `INSERT INTO menu (jenis_menu, nama_menu, jumlah_stok, harga) VALUES ('${req.body.jenis}', '${req.body.menu}', ${req.body.qty}, ${req.body.harga})`;
    client.query(query, (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }
        res.send(`Data dengan nama menu ${req.body.menu} berhasil dimasukkan.`);
    });
});

app.put('/menu', function(req, res) {
    const checkQuery = `SELECT id_menu FROM menu WHERE id_menu = ${req.body.id}`;
    client.query(checkQuery, (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }

        if (results.rows.length === 0) {
            res.statusCode = 404;
            res.send(`Data menu dengan ID ${req.body.id} tidak dapat ditemukan`);
        } else {
            const query = `UPDATE menu SET jenis_menu = '${req.body.jenis}', nama_menu = '${req.body.menu}', jumlah_stok = ${req.body.qty}, harga = ${req.body.harga} WHERE id_menu = ${req.body.id}`;
            client.query(query, (err, results) => {
                if (err) {
                    console.error(err);
                    res.send(null);
                    return;
                }
                res.send(`Data dengan ID ${req.body.id} berhasil di-update.`);
            });
        }
    });
});

app.post('/menu/:id', function(req, res) { // DELETE
    const checkQuery = `SELECT id_menu FROM menu WHERE id_menu = ${req.params.id}`;
    client.query(checkQuery, (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }

        if (results.rows.length === 0) {
            res.statusCode = 404;
            res.send(`Data menu dengan ID ${req.params.id} tidak dapat ditemukan`);
        } else {
            const query = `DELETE FROM menu WHERE id_menu = ${req.params.id}`;
            client.query(query, (err, results) => {
                if (err) {
                    console.error(err);
                    res.send(null);
                    return;
                }
                res.send(`Data dengan ID ${req.params.id} berhasil dihapus.`);
            });
        }
    });
});

app.post('/transaksi', function(req, res) {
    const query = `INSERT INTO penjualan (id_order, id_menu, jumlah_stok, date) VALUES (DEFAULT, '${req.body.id}', '${req.body.qty}', now())`;

    client.query(query, (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }
        res.send(`Data berhasil di-insert.`);
    });
});

app.get('/transaksi',(req, res) => {
    const query = `SELECT p.id_order, m.nama_menu, p.jumlah_stok, (p.jumlah_stok * m.harga) AS total_harga, p.date FROM penjualan p INNER JOIN menu m on m.id_menu = p.id_menu;`;
    client.query(query , (err, results) => {
        if (err) {
            console.error(err);
            res.send(null);
            return;
        }
        res.send(results.rows);
    });
});

//server listening
app.listen(port, () => {
    console.log(`Program sudah berjalan pada port ${port}`);
});