package com.example.minishopmanager

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "MiniShopPrefs"
        private const val KEY_USERNAME = "USERNAME"
        private const val KEY_REMEMBER = "REMEMBER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val savedRemember = prefs.getBoolean(KEY_REMEMBER, false)
        if (savedRemember) {
            etUsername.setText(prefs.getString(KEY_USERNAME, ""))
            cbRemember.isChecked = true
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_username), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = prefs.edit()
            if (cbRemember.isChecked) {
                editor.putString(KEY_USERNAME, username)
                editor.putBoolean(KEY_REMEMBER, true)
            } else {
                editor.clear()
            }
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
