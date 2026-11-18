package com.example.minishopmanager

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private val tag = "ProfileActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Log.d(tag, "onCreate call")

        val tvWelcomeProfile: TextView = findViewById(R.id.tvWelcomeProfile)
        val prefs = getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)
        val username = prefs.getString("USERNAME", "")

        if (!username.isNullOrEmpty()) {
            tvWelcomeProfile.text = getString(R.string.welcome_profile_user, username)
        } else {
            tvWelcomeProfile.text = getString(R.string.welcome_profile)
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
