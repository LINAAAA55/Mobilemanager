package com.example.minishopmanager

data class CartItem(
    val name: String,
    val price: Double,
    var qty: Int
)

object CartManager {
    const val CURRENCY = "DT"
    val items = mutableListOf<CartItem>()

    fun add(name: String, price: Double) {
        val existing = items.find { it.name == name }
        if (existing != null) existing.qty++ else items.add(CartItem(name, price, 1))
    }

    fun total() = items.sumOf { it.price * it.qty }
    fun count() = items.sumOf { it.qty }
    fun clear() = items.clear()
}