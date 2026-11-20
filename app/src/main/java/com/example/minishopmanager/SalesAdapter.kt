package com.example.minishopmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SalesAdapter(
    private val sales: List<Sale>
) : RecyclerView.Adapter<SalesAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val id: TextView = v.findViewById(R.id.tvSaleId)
        val date: TextView = v.findViewById(R.id.tvSaleDate)
        val total: TextView = v.findViewById(R.id.tvSaleTotal)
        val summary: TextView = v.findViewById(R.id.tvSaleSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false))

    override fun getItemCount() = sales.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val sale = sales[position]
        h.id.text = "Sale #${sale.id}"
        h.date.text = sale.date
        h.total.text = "%.2f %s".format(sale.total, CartManager.CURRENCY)
        h.summary.text = sale.itemsSummary
    }
}
