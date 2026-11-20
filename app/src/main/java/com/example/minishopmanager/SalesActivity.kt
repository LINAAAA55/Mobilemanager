package com.example.minishopmanager

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SalesActivity : AppCompatActivity() {

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)
        supportActionBar?.title = "Sales History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DBHelper(this)

        val tvTotalRevenue: TextView = findViewById(R.id.tvTotalRevenue)
        val tvEmptySales: TextView = findViewById(R.id.tvEmptySales)
        val rvSales: RecyclerView = findViewById(R.id.rvSales)

        val sales = db.getSales()
        val totalRevenue = db.getTotalRevenue()

        tvTotalRevenue.text = "Total Revenue: %.2f %s".format(totalRevenue, CartManager.CURRENCY)

        if (sales.isEmpty()) {
            tvEmptySales.visibility = View.VISIBLE
            rvSales.visibility = View.GONE
        } else {
            tvEmptySales.visibility = View.GONE
            rvSales.visibility = View.VISIBLE
            rvSales.layoutManager = LinearLayoutManager(this)
            rvSales.adapter = SalesAdapter(sales)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
