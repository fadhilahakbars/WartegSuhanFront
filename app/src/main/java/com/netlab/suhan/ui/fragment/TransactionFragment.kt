package com.netlab.suhan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.data.Order
import com.netlab.suhan.data.Transaction
import com.netlab.suhan.databinding.FragmentTransactionBinding
import com.netlab.suhan.ui.LoginActivity
import com.netlab.suhan.ui.adapter.TransactionAdapter
import com.netlab.suhan.utils.DialogCollection
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TransactionFragment : Fragment() {
    private var _binding : FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTransaction.visibility = View.GONE

        val responseListener = Response.Listener<String> { response ->
            val list = ArrayList<Transaction>()
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val local = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val json = JSONArray(response)
                for (i: Int in 0 until json.length()) {
                    val jsonTransaction = json.getJSONObject(i)

                    val date = formatter.parse(jsonTransaction.getString("date"))

                    val transaction = Transaction(
                        jsonTransaction.getInt("id_order"),
                        jsonTransaction.getString("nama_menu"),
                        if (date != null) local.format(date) else jsonTransaction.getString("date"),
                        jsonTransaction.getInt("jumlah_stok"),
                        jsonTransaction.getDouble("total_harga")
                    )
                    list.add(transaction)
                }

                val transactionAdapter = TransactionAdapter()
                transactionAdapter.setList(list)

                binding.progressBar.visibility = View.GONE
                binding.rvTransaction.visibility = View.VISIBLE

                with(binding.rvTransaction) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = transactionAdapter
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                DialogCollection.displayErrorDialog(requireContext(), "Data gagal untuk dibaca.")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                DialogCollection.displayErrorDialog(requireContext(), "Terdapat data yang tidak sesuai dengan aplikasi ini.")
            }
        }

        val errorListener = Response.ErrorListener { error ->
            binding.progressBar.visibility = View.GONE
            try {
                val errorCode = error.networkResponse.statusCode

                DialogCollection.displayErrorDialog(requireContext(), "Error Code $errorCode.")

                binding.progressBar.visibility = View.GONE
            } catch (e: NullPointerException) {
                DialogCollection.displayErrorDialog(requireContext(), "Tidak dapat menghubungi ke server")
            }
        }

        val queue = Volley.newRequestQueue(context)
        val request = WebRequest(Request.Method.GET, LoginActivity.URL + "/transaksi", responseListener, errorListener, HashMap())
        queue.add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}