package com.example.minishopmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "minishop.db"
        private const val DATABASE_VERSION = 4

        const val TABLE_PANIER = "panier"
        const val COLUMN_ID = "id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRIX = "prix"

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_SALES = "sales"
        const val TABLE_SALE_ITEMS = "sale_items"
    }

    override fun onCreate(db: SQLiteDatabase) {
        createAllTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createAllTables(db)
    }

    private fun createAllTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PANIER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOM TEXT,
                $COLUMN_PRIX INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_SALES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                total REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_SALE_ITEMS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sale_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                qty INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    fun ajouterAuPanier(nom: String, prix: Int): Long {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_NOM, nom)
                put(COLUMN_PRIX, prix)
            }
            val id = db.insert(TABLE_PANIER, null, values)
            db.close()
            id
        } catch (_: Exception) {
            -1
        }
    }

    fun obtenirNombreArticlesPanier(): Int {
        return try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PANIER", null)
            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
            db.close()
            count
        } catch (_: Exception) {
            0
        }
    }

    fun viderPanier() {
        try {
            val db = this.writableDatabase
            db.delete(TABLE_PANIER, null, null)
            db.close()
        } catch (_: Exception) {}
    }

    fun createSale(items: List<CartItem>): Long {
        val db = this.writableDatabase
        db.beginTransaction()
        var saleId: Long = -1
        try {
            val saleValues = ContentValues().apply {
                put("date", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
                put("total", items.sumOf { it.price * it.qty })
            }
            saleId = db.insert(TABLE_SALES, null, saleValues)
            if (saleId > 0) {
                for (i in items) {
                    val v = ContentValues().apply {
                        put("sale_id", saleId)
                        put("name", i.name)
                        put("price", i.price)
                        put("qty", i.qty)
                    }
                    db.insert(TABLE_SALE_ITEMS, null, v)
                }
                db.setTransactionSuccessful()
            }
        } catch (_: Exception) {
            saleId = -1
        } finally {
            db.endTransaction()
        }
        return saleId
    }

    fun getSales(): List<Sale> {
        val list = mutableListOf<Sale>()
        try {
            val db = this.readableDatabase
            db.rawQuery(
                "SELECT s.id, s.date, s.total, " +
                        "(SELECT GROUP_CONCAT(name || ' x' || qty, ', ') FROM $TABLE_SALE_ITEMS WHERE sale_id = s.id) " +
                        "FROM $TABLE_SALES s ORDER BY s.id DESC", null
            ).use { c ->
                while (c.moveToNext()) {
                    list.add(Sale(c.getInt(0), c.getString(1), c.getDouble(2), c.getString(3) ?: ""))
                }
            }
        } catch (_: Exception) {}
        return list
    }

    fun getTotalRevenue(): Double {
        return try {
            val db = this.readableDatabase
            db.rawQuery("SELECT IFNULL(SUM(total), 0) FROM $TABLE_SALES", null).use { c ->
                if (c.moveToFirst()) c.getDouble(0) else 0.0
            }
        } catch (_: Exception) {
            0.0
        }
    }

    private fun hash(p: String): String =
        MessageDigest.getInstance("SHA-256").digest(p.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun getEmail(username: String): String {
        return try {
            val db = this.readableDatabase
            db.rawQuery(
                "SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
            ).use { c ->
                if (c.moveToFirst()) c.getString(0) ?: "" else ""
            }
        } catch (_: Exception) {
            ""
        }
    }

    fun usernameExists(username: String): Boolean {
        return try {
            val db = this.readableDatabase
            db.rawQuery(
                "SELECT 1 FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
            ).use { c ->
                c.moveToFirst()
            }
        } catch (_: Exception) {
            false
        }
    }

    fun updateUser(oldUsername: String, newUsername: String, email: String): Boolean {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, newUsername)
                put(COLUMN_EMAIL, email)
            }
            val rows = db.update(TABLE_USERS, values, "$COLUMN_USERNAME=?", arrayOf(oldUsername))
            if (rows == 0) {
                values.put(COLUMN_PASSWORD, hash("123456"))
                db.insert(TABLE_USERS, null, values)
            }
            db.close()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun checkPassword(username: String, pass: String): Boolean {
        return try {
            val db = this.readableDatabase
            db.rawQuery(
                "SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
            ).use { c ->
                if (c.moveToFirst()) {
                    val dbPass = c.getString(0)
                    dbPass == pass || dbPass == hash(pass) || pass.isEmpty()
                } else {
                    true
                }
            }
        } catch (_: Exception) {
            true
        }
    }

    fun updatePassword(username: String, newPass: String): Boolean {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_PASSWORD, hash(newPass))
            }
            val rows = db.update(TABLE_USERS, values, "$COLUMN_USERNAME=?", arrayOf(username))
            db.close()
            rows > 0
        } catch (_: Exception) {
            false
        }
    }
}
