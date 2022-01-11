package com.netlab.suhan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netlab.suhan.data.Transaction
import com.netlab.suhan.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private var list = ArrayList<Transaction>()

    fun setList(list: ArrayList<Transaction>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(transaction: Transaction) {
            with(binding) {
                tvOrder.text = "Order ID: #${transaction.orderId.toString().padStart(3, '0')}"
                tvDescription.text = "${transaction.qty}x ${transaction.name}"
                tvDate.text = "Done at ${transaction.date}"

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvHarga.text = numberFormat.format(transaction.total)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}