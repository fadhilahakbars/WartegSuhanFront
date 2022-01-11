package com.netlab.suhan.ui.fragment.stock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.data.Menu
import com.netlab.suhan.data.MenuType
import com.netlab.suhan.databinding.FragmentStockBinding
import com.netlab.suhan.ui.LoginActivity
import com.netlab.suhan.ui.adapter.StockAdapter
import com.netlab.suhan.utils.DialogCollection
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException
import java.lang.NullPointerException

class StockFragment : Fragment() {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), EditStockActivity::class.java)
            intent.putExtra(EditStockActivity.EXTRA_EDIT_MODE, false)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == EditStockActivity.RESULT_CODE) {
            fetchData()
        }
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvStockList.visibility = View.GONE
        binding.fabAdd.visibility = View.GONE

        val responseListener = Response.Listener<String> { response ->
            val list = ArrayList<Menu>()
            try {
                val json = JSONArray(response)
                for (i: Int in 0 until json.length()) {
                    val jsonMenu = json.getJSONObject(i)

                    val menu = Menu(
                        jsonMenu.getInt("id_menu"),
                        MenuType.valueOf(jsonMenu.getString("jenis_menu")),
                        jsonMenu.getString("nama_menu"),
                        jsonMenu.getInt("jumlah_stok"),
                        jsonMenu.getDouble("harga")
                    )
                    list.add(menu)
                }

                val stockAdapter = StockAdapter()
                stockAdapter.setList(list)

                binding.progressBar.visibility = View.GONE
                binding.rvStockList.visibility = View.VISIBLE
                binding.fabAdd.visibility = View.VISIBLE

                with(binding.rvStockList) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = stockAdapter
                }

                stockAdapter.onDataSelected = { item ->
                    val intent = Intent(requireContext(), EditStockActivity::class.java)
                    intent.putExtra(EditStockActivity.EXTRA_STOCK_ACTIVITY, item)
                    intent.putExtra(EditStockActivity.EXTRA_EDIT_MODE, true)
                    startActivityForResult(intent, 100)
                }
            } catch (e: JSONException) {
                DialogCollection.displayErrorDialog(requireContext(), "Data gagal untuk dibaca.")
            } catch (e: IllegalArgumentException) {
                DialogCollection.displayErrorDialog(requireContext(), "Terdapat data yang tidak sesuai dengan aplikasi ini.")
            }
        }

        val errorListener = Response.ErrorListener { error ->
            try {
                binding.progressBar.visibility = View.GONE
                val errorCode = error.networkResponse.statusCode

                DialogCollection.displayErrorDialog(requireContext(), "Error Code $errorCode")
            } catch (e: NullPointerException) {
                DialogCollection.displayErrorDialog(requireContext(), "Tidak dapat menghubungi ke server")
            }
        }

        val queue = Volley.newRequestQueue(requireContext())
        val request = WebRequest(Request.Method.GET, LoginActivity.URL + "/menu", responseListener, errorListener, HashMap())
        queue.add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}