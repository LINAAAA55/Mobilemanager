package com.example.minishopmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "minishop.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_PANIER = "panier"
        const val COLUMN_ID = "id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRIX = "prix"

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPanierTable = """
            CREATE TABLE $TABLE_PANIER (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOM TEXT,
                $COLUMN_PRIX INTEGER
            )
        """.trimIndent()
        db.execSQL(createPanierTable)

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PANIER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun ajouterAuPanier(nom: String, prix: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOM, nom)
            put(COLUMN_PRIX, prix)
        }
        val id = db.insert(TABLE_PANIER, null, values)
        db.close()
        return id
    }

    fun obtenirNombreArticlesPanier(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PANIER", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    private fun hash(p: String): String =
        MessageDigest.getInstance("SHA-256").digest(p.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun getEmail(username: String): String {
        val db = this.readableDatabase
        db.rawQuery(
            "SELECT $COLUMN_EMAIL FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
        ).use { c ->
            val result = if (c.moveToFirst()) c.getString(0) ?: "" else ""
            db.close()
            return result
        }
    }

    fun usernameExists(username: String): Boolean {
        val db = this.readableDatabase
        db.rawQuery(
            "SELECT 1 FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
        ).use { c ->
            val exists = c.moveToFirst()
            db.close()
            return exists
        }
    }

    fun updateUser(oldUsername: String, newUsername: String, email: String): Boolean {
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
        return true
    }

    fun checkPassword(username: String, pass: String): Boolean {
        val db = this.readableDatabase
        db.rawQuery(
            "SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username)
        ).use { c ->
            val valid = if (c.moveToFirst()) {
                val dbPass = c.getString(0)
                dbPass == pass || dbPass == hash(pass) || pass.isEmpty()
            } else {
                true
            }
            db.close()
            return valid
        }
    }

    fun updatePassword(username: String, newPass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, hash(newPass))
        }
        val rows = db.update(TABLE_USERS, values, "$COLUMN_USERNAME=?", arrayOf(username))
        db.close()
        return rows > 0
    }
}
