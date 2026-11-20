package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: CartAdapter
    private lateinit var rv: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        supportActionBar?.title = "Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = DBHelper(this)
        rv = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnCheckout = findViewById(R.id.btnCheckout)

        adapter = CartAdapter(CartManager.items) { refresh() }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                if (pos != RecyclerView.NO_POSITION && pos < CartManager.items.size) {
                    CartManager.items.removeAt(pos)
                    adapter.notifyItemRemoved(pos)
                    refresh()
                }
            }
        }).attachToRecyclerView(rv)

        btnCheckout.setOnClickListener { confirmCheckout() }
        findViewById<Button>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }
        refresh()
    }

    private fun refresh() {
        val empty = CartManager.items.isEmpty()
        tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        rv.visibility = if (empty) View.GONE else View.VISIBLE
        btnCheckout.isEnabled = !empty
        tvTotal.text = "Total: %.2f %s".format(CartManager.total(), CartManager.CURRENCY)
    }

    private fun confirmCheckout() {
        AlertDialog.Builder(this)
            .setTitle("Confirm sale")
            .setMessage(
                "%d item(s)\nTotal: %.2f %s".format(
                    CartManager.count(), CartManager.total(), CartManager.CURRENCY
                )
            )
            .setPositiveButton("Confirm") { _, _ -> checkout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkout() {
        val saleId = db.createSale(CartManager.items.toList())
        if (saleId > 0) {
            CartManager.clear()
            db.viderPanier()
            Toast.makeText(this, "Sale #$saleId saved", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SalesActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Could not save the sale", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
