package com.netlab.suhan.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.netlab.suhan.data.Menu
import com.netlab.suhan.databinding.FragmentCashierBinding
import com.netlab.suhan.ui.LoginActivity
import com.netlab.suhan.ui.fragment.stock.DialogItem
import com.netlab.suhan.utils.DialogCollection
import com.netlab.suhan.utils.PriceFormatter
import com.netlab.suhan.utils.WebRequest
import java.lang.NullPointerException

class CashierFragment : Fragment() {
    private var _binding: FragmentCashierBinding? = null
    private val binding get() = _binding!!

    private var menuId = 0
    private lateinit var menu: Menu
    private var total: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashierBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutMenu.setEndIconOnClickListener { showDialogItem() }
        binding.etMenu.setOnClickListener { showDialogItem() }

        binding.btnCount.setOnClickListener {
            if (checkRequirement() && menuId != 0) {
                try {
                    val price = menu.price
                    with(binding) {
                        btnCount.visibility = View.GONE
                        lblTotal.visibility = View.VISIBLE
                        tvTotal.visibility = View.VISIBLE
                        btnOrder.visibility = View.VISIBLE
                        layoutMenu.isEndIconVisible = false
                        etStock.isEnabled = false

                        total = etStock.text.toString().toInt() * price
                        tvTotal.text = "Rp${PriceFormatter.format(total)},00"
                    }
                } catch (e: UninitializedPropertyAccessException) {
                    DialogCollection.displayErrorDialog(requireContext(), "Terdapat kesalahan membaca data menu")
                }
            }
        }

        binding.btnOrder.setOnClickListener {
            val responseListener = Response.Listener<String> { response ->
                if (response != null && response.isNotBlank()) {
                    Snackbar.make(view, "Data barhasil ditambahkan", Snackbar.LENGTH_LONG)
                        .show()
                    with(binding) {
                        btnCount.visibility = View.VISIBLE
                        lblTotal.visibility = View.GONE
                        tvTotal.visibility = View.GONE
                        btnOrder.visibility = View.GONE
                        btnOrder.isEnabled = true
                        layoutMenu.isEndIconVisible = true
                        etStock.isEnabled = true
                        progressBar.visibility = View.GONE

                        menuId = 0
                        total = 0.0
                        etStock.setText("")
                        etMenu.setText("")
                        etHarga.setText("0")
                        tvTotal.text = "Rp0,00"
                    }
                } else {
                    DialogCollection.displayErrorDialog(requireContext(), "Data gagal untuk dikirim.")
                }
            }

            val params = HashMap<String, String>()
            params["id"] = menuId.toString()
            params["qty"] = binding.etStock.text.toString()

            val errorListener = Response.ErrorListener { error ->
                binding.progressBar.visibility = View.GONE
                try {
                    val errorCode = error.networkResponse.statusCode

                    DialogCollection.displayErrorDialog(requireContext(), "Error Code $errorCode.")

                    binding.progressBar.visibility = View.GONE
                } catch (e: NullPointerException) {
                    DialogCollection.displayErrorDialog(requireContext(), "Tidak dapat menghubungi ke server")
                } finally {
                    with(binding) {
                        btnCount.visibility = View.VISIBLE
                        lblTotal.visibility = View.GONE
                        tvTotal.visibility = View.GONE
                        btnOrder.isEnabled = true
                        btnOrder.visibility = View.GONE
                        layoutMenu.isEndIconVisible = true
                        etStock.isEnabled = true
                        progressBar.visibility = View.GONE
                    }
                }
            }

            binding.btnOrder.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            val queue = Volley.newRequestQueue(requireContext())
            val request = WebRequest(Request.Method.POST, LoginActivity.URL + "/transaksi", responseListener, errorListener, params)
            queue.add(request)
        }
    }

    fun checkRequirement() : Boolean {
        with(binding) {
            val map = mapOf(
                etMenu to layoutMenu,
                etStock to layoutStock,
            )

            map.forEach { (_, value) -> value.error = "" }
            val result = map.filter { (key, _) -> key.text.toString().isBlank() }

            if (binding.etStock.text.toString().isNotBlank() && binding.etStock.text.toString().toInt() > menu.stock) {
                layoutStock.error = "Stock tidak mencukupi"
                return false
            }

            return if (result.isNotEmpty()) {
                val error = "Wajib diisi!"
                result.forEach { (_, value) -> value.error = error}
                false
            } else true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialogItem() {
        val dialog = DialogItem()
        dialog.onDataSelected = { data ->
            menu = data
            menuId = data.id
            binding.etMenu.setText(data.name)
            if (binding.etStock.text.toString().isBlank()) binding.etStock.setText("1")
            binding.etHarga.setText(PriceFormatter.format(data.price))
        }
        dialog.show(childFragmentManager, null)
    }
}