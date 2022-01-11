package com.netlab.suhan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netlab.suhan.data.Order
import com.netlab.suhan.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    private var list = ArrayList<Order>()

    fun setList(list: ArrayList<Order>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            with(binding) {
                tvNama.text = order.menu.uppercase()
                tvQty.text = "Qty: ${order.qty}"

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvHarga.text = numberFormat.format(order.price)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}