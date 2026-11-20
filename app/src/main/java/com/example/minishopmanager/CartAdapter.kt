package com.example.minishopmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvName)
        val price: TextView = v.findViewById(R.id.tvPrice)
        val qty: TextView = v.findViewById(R.id.tvQty)
        val line: TextView = v.findViewById(R.id.tvLineTotal)
        val minus: ImageButton = v.findViewById(R.id.btnMinus)
        val plus: ImageButton = v.findViewById(R.id.btnPlus)
        val delete: ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = items[position]
        h.name.text = item.name
        h.price.text = "%.2f %s".format(item.price, CartManager.CURRENCY)
        h.qty.text = item.qty.toString()
        h.line.text = "%.2f %s".format(item.price * item.qty, CartManager.CURRENCY)

        h.plus.setOnClickListener {
            val pos = h.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            item.qty++
            notifyItemChanged(pos)
            onChange()
        }
        h.minus.setOnClickListener {
            val pos = h.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            if (item.qty > 1) {
                item.qty--
                notifyItemChanged(pos)
            } else {
                items.removeAt(pos)
                notifyItemRemoved(pos)
            }
            onChange()
        }
        h.delete.setOnClickListener {
            val pos = h.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            items.removeAt(pos)
            notifyItemRemoved(pos)
            onChange()
        }
    }
}
