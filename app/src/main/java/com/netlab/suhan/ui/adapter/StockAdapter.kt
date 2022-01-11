package com.netlab.suhan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netlab.suhan.data.Menu
import com.netlab.suhan.databinding.ItemMenuBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class StockAdapter : RecyclerView.Adapter<StockAdapter.ViewHolder>() {
    var onDataSelected: ((Menu) -> Unit)? = null
    private var list = ArrayList<Menu>()

    fun setList(list: ArrayList<Menu>) {
        this.list = list
    }

    inner class ViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(menu: Menu) {
            with(binding) {
                tvType.text = menu.type.toString()
                tvNama.text = menu.name.uppercase()
                tvQty.text = "Sisa Stok: ${menu.stock}"

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvHarga.text = "Harga: ${numberFormat.format(menu.price)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onDataSelected?.invoke(list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}