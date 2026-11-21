package com.example.minishopmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"
    private lateinit var dbHelper: DBHelper

    private var allProducts = listOf<Product>()
    private var allArrivesSoon = listOf<Product>()

    private var filteredProducts = mutableListOf<Product>()
    private var filteredArrivesSoon = mutableListOf<Product>()

    private var currentCategory = "Tous"
    private var currentSearchQuery = ""

    private lateinit var adapterProducts: ProductAdapter
    private lateinit var adapterArrivesSoon: ProductAdapter

    private lateinit var listViewProducts: ListView
    private lateinit var listViewArrivesSoon: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedPreferences()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate call")

        dbHelper = DBHelper(this)

        listViewProducts = findViewById(R.id.listViewProducts)
        listViewArrivesSoon = findViewById(R.id.listViewArrivesSoon)

        val (prodList, arrivesList) = lireProduitsDepuisJSON()
        allProducts = prodList
        allArrivesSoon = arrivesList

        filteredProducts.addAll(allProducts)
        filteredArrivesSoon.addAll(allArrivesSoon)

        adapterProducts = ProductAdapter(this, filteredProducts)
        listViewProducts.adapter = adapterProducts

        adapterArrivesSoon = ProductAdapter(this, filteredArrivesSoon)
        listViewArrivesSoon.adapter = adapterArrivesSoon

        setListViewHeightBasedOnChildren(listViewProducts)
        setListViewHeightBasedOnChildren(listViewArrivesSoon)

        listViewProducts.setOnItemClickListener { _, _, position, _ ->
            if (position in filteredProducts.indices) {
                val selectedProduct = filteredProducts[position]
                dbHelper.ajouterAuPanier(selectedProduct.name, selectedProduct.price)
                CartManager.add(selectedProduct.name, selectedProduct.price.toDouble())

                val message = String.format(getString(R.string.product_added_cart), selectedProduct.name)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        listViewArrivesSoon.setOnItemClickListener { _, _, position, _ ->
            if (position in filteredArrivesSoon.indices) {
                val selectedProduct = filteredArrivesSoon[position]
                val message = String.format(getString(R.string.arriving_soon_toast), selectedProduct.name)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        val etSearchProduct: EditText = findViewById(R.id.etSearchProduct)
        etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                filterData()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupCategoryButtons()

        val btnToggleDarkMode: Button = findViewById(R.id.btnToggleDarkMode)
        btnToggleDarkMode.setOnClickListener {
            toggleDarkMode()
        }

        val btnToggleLanguage: Button = findViewById(R.id.btnToggleLanguage)
        btnToggleLanguage.setOnClickListener {
            toggleLanguage()
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

    private fun setupCategoryButtons() {
        val btnCatAll: Button = findViewById(R.id.btnCatAll)
        val btnCatPhones: Button = findViewById(R.id.btnCatPhones)
        val btnCatTech: Button = findViewById(R.id.btnCatTech)
        val btnCatAcc: Button = findViewById(R.id.btnCatAcc)
        val btnCatCosmetics: Button = findViewById(R.id.btnCatCosmetics)

        val catButtons = listOf(btnCatAll, btnCatPhones, btnCatTech, btnCatAcc, btnCatCosmetics)

        btnCatAll.setOnClickListener { selectCategory("Tous", catButtons, btnCatAll) }
        btnCatPhones.setOnClickListener { selectCategory("Smartphones", catButtons, btnCatPhones) }
        btnCatTech.setOnClickListener { selectCategory("High-Tech", catButtons, btnCatTech) }
        btnCatAcc.setOnClickListener { selectCategory("Accessoires", catButtons, btnCatAcc) }
        btnCatCosmetics.setOnClickListener { selectCategory("Cosmétiques", catButtons, btnCatCosmetics) }
    }

    private fun selectCategory(category: String, allButtons: List<Button>, selectedButton: Button) {
        currentCategory = category
        allButtons.forEach {
            it.backgroundTintList = getColorStateList(R.color.orange_light)
        }
        selectedButton.backgroundTintList = getColorStateList(R.color.red_primary)
        filterData()
    }

    private fun filterData() {
        filteredProducts.clear()
        filteredArrivesSoon.clear()

        val query = currentSearchQuery.lowercase(Locale.getDefault())

        allProducts.forEach { p ->
            val matchesCategory = (currentCategory == "Tous" || p.category.equals(currentCategory, ignoreCase = true))
            val matchesSearch = query.isEmpty() || p.name.lowercase(Locale.getDefault()).contains(query)
            if (matchesCategory && matchesSearch) {
                filteredProducts.add(p)
            }
        }

        allArrivesSoon.forEach { p ->
            val matchesCategory = (currentCategory == "Tous" || currentCategory == "Smartphones" || p.category.equals(currentCategory, ignoreCase = true))
            val matchesSearch = query.isEmpty() || p.name.lowercase(Locale.getDefault()).contains(query)
            if (matchesCategory && matchesSearch) {
                filteredArrivesSoon.add(p)
            }
        }

        adapterProducts.notifyDataSetChanged()
        adapterArrivesSoon.notifyDataSetChanged()

        setListViewHeightBasedOnChildren(listViewProducts)
        setListViewHeightBasedOnChildren(listViewArrivesSoon)
    }

    private fun applySavedPreferences() {
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        val isDarkMode = prefs.getBoolean("DARK_MODE", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val lang = prefs.getString("LANG", "fr") ?: "fr"
        val localeList = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun toggleDarkMode() {
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DARK_MODE", false)
        val newMode = !isDarkMode

        prefs.edit().putBoolean("DARK_MODE", newMode).apply()

        if (newMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun toggleLanguage() {
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("LANG", "fr") ?: "fr"
        val newLang = if (currentLang == "fr") "ar" else "fr"

        prefs.edit().putString("LANG", newLang).apply()

        val localeList = LocaleListCompat.forLanguageTags(newLang)
        AppCompatDelegate.setApplicationLocales(localeList)
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
                    val category = if (item.has("categorie")) item.getString("categorie") else "Tous"

                    val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
                    val finalResId = if (imageResId != 0) imageResId else R.drawable.ic_phone

                    products.add(Product(id, name, price, finalResId, category))
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
                    val category = if (item.has("categorie")) item.getString("categorie") else "Smartphones"

                    val imageResId = resources.getIdentifier(imageName, "drawable", packageName)
                    val finalResId = if (imageResId != 0) imageResId else R.drawable.ic_phone

                    arrivesSoon.add(Product(id, name, price, finalResId, category))
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Erreur lecture produits.json", e)
        }

        if (products.isEmpty()) {
            products.addAll(
                listOf(
                    Product(1, "Téléphone", 1200, R.drawable.ic_phone, "Smartphones"),
                    Product(2, "Casque Bluetooth", 150, R.drawable.ic_headphones, "High-Tech"),
                    Product(3, "Montre connectée", 450, R.drawable.ic_watch, "High-Tech"),
                    Product(4, "Chargeur USB", 45, R.drawable.ic_charger, "Accessoires"),
                    Product(5, "Accessoires", 80, R.drawable.ic_accessories, "Accessoires"),
                    Product(6, "Cosmétiques", 110, R.drawable.ic_cosmetics, "Cosmétiques")
                )
            )
        }

        if (arrivesSoon.isEmpty()) {
            arrivesSoon.addAll(
                listOf(
                    Product(101, "iPhone 16 Pro", 4500, R.drawable.ic_phone, "Smartphones"),
                    Product(102, "Samsung Galaxy S25 Ultra", 4200, R.drawable.ic_phone, "Smartphones"),
                    Product(103, "Google Pixel 9 Pro", 3400, R.drawable.ic_phone, "Smartphones"),
                    Product(104, "Xiaomi 15 Ultra", 2900, R.drawable.ic_phone, "Smartphones")
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
