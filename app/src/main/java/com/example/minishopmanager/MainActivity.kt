package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate call")

        val listViewProducts: ListView = findViewById(R.id.listViewProducts)

        val productList = listOf(
            Product("Téléphone", R.drawable.ic_phone),
            Product("Casque Bluetooth", R.drawable.ic_headphones),
            Product("Montre connectée", R.drawable.ic_watch),
            Product("Chargeur USB", R.drawable.ic_charger),
            Product("Accessoires", R.drawable.ic_accessories),
            Product("Cosmétiques", R.drawable.ic_cosmetics)
        )

        val adapter = ProductAdapter(this, productList)
        listViewProducts.adapter = adapter

        listViewProducts.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = productList[position]
            val message = "${getString(R.string.product_selected_prefix)}${selectedProduct.name}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val btnSuivant: Button = findViewById(R.id.btnSuivant)
        btnSuivant.setOnClickListener {
            Toast.makeText(this, getString(R.string.hello_toast), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart call")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume call")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause call")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop call")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(tag, "onRestart call")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy call")
    }
}
