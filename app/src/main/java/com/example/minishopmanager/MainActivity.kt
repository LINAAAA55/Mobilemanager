package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
        val listViewArrivesSoon: ListView = findViewById(R.id.listViewArrivesSoon)

        val (productList, arrivesSoonList) = lireProduitsDepuisJSON()

        val adapterProducts = ProductAdapter(this, productList)
        listViewProducts.adapter = adapterProducts
        setListViewHeightBasedOnChildren(listViewProducts)

        listViewProducts.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = productList[position]

            dbHelper.ajouterAuPanier(selectedProduct.name, selectedProduct.price)
            CartManager.add(selectedProduct.name, selectedProduct.price.toDouble())

            val message = String.format(getString(R.string.product_added_cart), selectedProduct.name)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val adapterArrivesSoon = ProductAdapter(this, arrivesSoonList)
        listViewArrivesSoon.adapter = adapterArrivesSoon
        setListViewHeightBasedOnChildren(listViewArrivesSoon)

        listViewArrivesSoon.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = arrivesSoonList[position]
            val message = String.format(getString(R.string.arriving_soon_toast), selectedProduct.name)
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

    private fun lireProduitsDepuisJSON(): Pair<List<Product>, List<Product>> {
        val products = mutableListOf<Product>()
        val arrivesSoon = mutableListOf<Product>()

        try {
            val inputStream = assets.open("produits.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)

            if (jsonObject.has("produits")) {
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
            }

            if (jsonObject.has("arrivesSoon")) {
                val arrivesArray = jsonObject.getJSONArray("arrivesSoon")
                for (i in 0 until arrivesArray.length()) {
                    val item = arrivesArray.getJSONObject(i)
                    val id = item.getInt("id")
                    val name = item.getString("nom")
                    val price = item.getInt("prix")
                    val imageName = item.getString("image")

                    val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
                    val finalResId = if (imageResId != 0) imageResId else R.drawable.ic_phone

                    arrivesSoon.add(Product(id, name, price, finalResId))
                }
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

        if (arrivesSoon.isEmpty()) {
            arrivesSoon.addAll(
                listOf(
                    Product(101, "iPhone 16 Pro", 4500, R.drawable.ic_phone),
                    Product(102, "Samsung Galaxy S25 Ultra", 4200, R.drawable.ic_phone),
                    Product(103, "Google Pixel 9 Pro", 3400, R.drawable.ic_phone),
                    Product(104, "Xiaomi 15 Ultra", 2900, R.drawable.ic_phone)
                )
            )
        }

        return Pair(products, arrivesSoon)
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
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
