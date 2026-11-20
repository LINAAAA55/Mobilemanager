package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate call")

        dbHelper = DBHelper(this)

        val listViewProducts: ListView = findViewById(R.id.listViewProducts)

        val productList = lireProduitsDepuisJSON()

        val adapter = ProductAdapter(this, productList)
        listViewProducts.adapter = adapter

        listViewProducts.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = productList[position]

            // Insertion dans la base SQLite
            dbHelper.ajouterAuPanier(selectedProduct.name, selectedProduct.price)

            // Ajout au CartManager local
            CartManager.add(selectedProduct.name, selectedProduct.price.toDouble())

            val message = String.format(getString(R.string.product_added_cart), selectedProduct.name)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val btnSuivant: Button = findViewById(R.id.btnSuivant)
        btnSuivant.setOnClickListener {
            Toast.makeText(this, getString(R.string.hello_toast), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val btnCart: Button = findViewById(R.id.btnCart)
        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun lireProduitsDepuisJSON(): List<Product> {
        val products = mutableListOf<Product>()
        try {
            val inputStream = assets.open("produits.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("produits")

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val id = item.getInt("id")
                val name = item.getString("nom")
                val price = item.getInt("prix")
                val imageName = item.getString("image")

                val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
                val finalResId = if (imageResId != 0) imageResId else R.drawable.ic_phone

                products.add(Product(id, name, price, finalResId))
            }
        } catch (e: Exception) {
            Log.e(tag, "Erreur lecture produits.json", e)
        }

        if (products.isEmpty()) {
            products.addAll(
                listOf(
                    Product(1, "Téléphone", 1200, R.drawable.ic_phone),
                    Product(2, "Casque Bluetooth", 150, R.drawable.ic_headphones),
                    Product(3, "Montre connectée", 450, R.drawable.ic_watch),
                    Product(4, "Chargeur USB", 45, R.drawable.ic_charger),
                    Product(5, "Accessoires", 80, R.drawable.ic_accessories),
                    Product(6, "Cosmétiques", 110, R.drawable.ic_cosmetics)
                )
            )
        }
        return products
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
