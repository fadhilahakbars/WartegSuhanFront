package com.netlab.suhan.ui.fragment.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.netlab.suhan.R
import com.netlab.suhan.data.Menu
import com.netlab.suhan.data.MenuType
import com.netlab.suhan.databinding.ActivityEditStockBinding
import com.netlab.suhan.ui.LoginActivity
import com.netlab.suhan.utils.DialogCollection
import com.netlab.suhan.utils.PriceFormatter
import com.netlab.suhan.utils.WebRequest
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.HashMap

class EditStockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditStockBinding

    companion object {
        const val EXTRA_STOCK_ACTIVITY = "extra_data"
        const val EXTRA_EDIT_MODE = "extra_edit_mode"
        const val RESULT_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.list_stock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val menu = intent.getParcelableExtra<Menu>(EXTRA_STOCK_ACTIVITY)
        val mode = intent.getBooleanExtra(EXTRA_EDIT_MODE, false)
        if ((mode && menu != null) || !mode) {
            with(binding) {
                if (mode && menu != null) {
                    spMenuType.setSelection(
                        when (menu.type) {
                            MenuType.Makanan -> 0
                            MenuType.Minuman -> 1
                            MenuType.Paket -> 2
                        }
                    )
                    etMenuName.setText(menu.name)
                    etStock.setText(menu.stock.toString())
                    etHarga.setText(PriceFormatter.format(menu.price))

                    btnDelete.visibility = View.VISIBLE
                    btnDelete.setOnClickListener {
                        AlertDialog.Builder(this@EditStockActivity)
                            .setTitle("Perhatian")
                            .setMessage("Apakah Anda yakin untuk menghapus data ini? Data yang sudah dihapus tidak dapat kembali.")
                            .setPositiveButton("Ya") { _, _ ->
                                retrieveDelete(menu.id)
                            }
                            .setNegativeButton("Tidak") { _, _ -> }
                            .show()
                    }
                }

                btnSave.setOnClickListener {
                    with(binding) {
                        etMenuName.isEnabled = false
                        etStock.isEnabled = false
                        etHarga.isEnabled = false
                    }

                    if (checkRequirement()) {
                        val params = HashMap<String, String>()
                        params["jenis"] = when (spMenuType.selectedItemPosition) {
                            0 -> MenuType.Makanan.name
                            1 -> MenuType.Minuman.name
                            2 -> MenuType.Paket.name
                            else -> MenuType.Makanan.name
                        }
                        params["menu"] = etMenuName.text.toString()
                        params["qty"] = etStock.text.toString()
                        params["harga"] = etHarga.text.toString().replace(".", "")
                        if (mode && menu != null) {
                            // Edit
                            params["id"] = menu.id.toString()
                            retrieveUpdate(params)
                        } else {
                            retrieveInsert(params)
                        }
                    }

                    with(binding) {
                        etMenuName.isEnabled = true
                        etStock.isEnabled = true
                        etHarga.isEnabled = true
                    }
                }
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Terdapat kesalahan dalam membaca data")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Perhatian")
            .setMessage("Apakah Anda ingin membatalkan perubahan ini?")
            .setPositiveButton("Ya") { _, _  -> finish() }
            .setNegativeButton("Tidak") { _, _ -> }
            .show()
    }

    fun checkRequirement() : Boolean {
        with(binding) {
            val map = mapOf(
                etMenuName to layoutMenuName,
                etStock to layoutStock,
                etHarga to layoutHarga
            )

            map.forEach { (_, value) -> value.error = "" }
            val result = map.filter { (key, _) -> key.text.toString().isBlank() }

            return if (result.isNotEmpty()) {
                val error = "Wajib diisi!"
                result.forEach { (_, value) -> value.error = error}
                false
            } else true
        }
    }

    private fun retrieveDelete(id: Int) {
        val responseListener = Response.Listener<String> { response ->
            if (response != null && response.isNotBlank()) {
                Toast.makeText(this, "Data berhasil dihapus dari database", Toast.LENGTH_LONG)
                    .show()
                setResult(RESULT_CODE)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Perintah hapus gagal dilakukan.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            try {
                val errorCode = error.networkResponse.statusCode
                if (errorCode == 404) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Data dengan ID $id tidak dapat ditemukan.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Error Code $errorCode.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
            } catch (e: NullPointerException) {
                DialogCollection.displayErrorDialog(this, "Tidak dapat menghubungi ke server")
            }
        }

        val queue = Volley.newRequestQueue(this)
        val request = WebRequest(Request.Method.POST, LoginActivity.URL + "/menu/" + id, responseListener, errorListener, HashMap())
        queue.add(request)
    }

    private fun retrieveUpdate(params: HashMap<String, String>) {
        val responseListener = Response.Listener<String> { response ->
            if (response != null && response.isNotBlank()) {
                Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_LONG)
                    .show()
                setResult(RESULT_CODE)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Data gagal untuk dikirim.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            try {
                val errorCode = error.networkResponse.statusCode
                if (errorCode == 404) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Data dengan ID ${params["id"]} tidak dapat ditemukan.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Error Code $errorCode.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
            } catch (e: NullPointerException) {
                DialogCollection.displayErrorDialog(this, "Tidak dapat menghubungi ke server")
            }
        }

        val queue = Volley.newRequestQueue(this)
        val request = WebRequest(Request.Method.PUT, LoginActivity.URL + "/menu", responseListener, errorListener, params)
        queue.add(request)
    }

    private fun retrieveInsert(params: HashMap<String, String>) {
        val responseListener = Response.Listener<String> { response ->
            if (response != null && response.isNotBlank()) {
                Toast.makeText(this, "Data berhasil dimasukkan ke dalam database", Toast.LENGTH_LONG)
                    .show()
                setResult(RESULT_CODE)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Data gagal untuk dikirim.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Error Code ${error.networkResponse.statusCode}.")
                .setPositiveButton("OK") { _, _ -> }
                .show()
        }

        val queue = Volley.newRequestQueue(this)
        val request = WebRequest(Request.Method.POST, LoginActivity.URL + "/menu", responseListener, errorListener, params)
        queue.add(request)
    }
}