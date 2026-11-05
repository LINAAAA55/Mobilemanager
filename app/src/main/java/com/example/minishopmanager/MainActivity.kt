package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(tag, "onCreate call")

        val btnSuivant: Button = findViewById(R.id.btnSuivant)
        btnSuivant.setOnClickListener {
            // Affichage du Toast
            Toast.makeText(this, getString(R.string.hello_toast), Toast.LENGTH_SHORT).show()

            // Navigation vers ProfileActivity avec un Intent explicite
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
