package com.example.minishopmanager

import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private val tag = "ProfileActivityLifecycle"
    private lateinit var db: DBHelper
    private lateinit var username: String

    private lateinit var imgProfile: ImageView
    private lateinit var tvHeader: TextView
    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etOldPass: TextInputEditText
    private lateinit var etNewPass: TextInputEditText
    private lateinit var etConfirmPass: TextInputEditText

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { savePhoto(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Log.d(tag, "onCreate call")

        db = DBHelper(this)
        val prefs = getSharedPreferences("MiniShopPrefs", Context.MODE_PRIVATE)
        username = prefs.getString("USERNAME", null) ?: "Lina"

        imgProfile = findViewById(R.id.imgProfile)
        tvHeader = findViewById(R.id.tvHeader)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etOldPass = findViewById(R.id.etOldPass)
        etNewPass = findViewById(R.id.etNewPass)
        etConfirmPass = findViewById(R.id.etConfirmPass)

        imgProfile.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
        imgProfile.clipToOutline = true

        loadProfile()

        imgProfile.setOnClickListener { pickImage.launch("image/*") }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveInfo() }
        findViewById<Button>(R.id.btnChangePass).setOnClickListener { changePassword() }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes") { _, _ -> logout() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadProfile() {
        tvHeader.text = username
        etUsername.setText(username)
        etEmail.setText(db.getEmail(username))
        val photo = photoFile()
        if (photo.exists()) imgProfile.setImageURI(Uri.fromFile(photo))
    }

    private fun photoFile() = File(filesDir, "profile_${username}.jpg")

    private fun savePhoto(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                photoFile().outputStream().use { input.copyTo(it) }
            }
            imgProfile.setImageURI(null)
            imgProfile.setImageURI(Uri.fromFile(photoFile()))
        } catch (e: Exception) {
            toast("Could not save photo")
        }
    }

    private fun saveInfo() {
        val newName = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (newName.length < 3) { etUsername.error = "Min 3 characters"; return }
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email"; return
        }
        if (newName != username && db.usernameExists(newName)) {
            etUsername.error = "Username already taken"; return
        }

        if (db.updateUser(username, newName, email)) {
            if (newName != username) {
                val old = photoFile()
                getSharedPreferences("MiniShopPrefs", Context.MODE_PRIVATE)
                    .edit().putString("USERNAME", newName).apply()
                username = newName
                if (old.exists()) old.renameTo(photoFile())
                tvHeader.text = username
            }
            toast("Profile updated")
        } else toast("Update failed")
    }

    private fun changePassword() {
        val old = etOldPass.text.toString()
        val new = etNewPass.text.toString()
        val confirm = etConfirmPass.text.toString()

        if (!db.checkPassword(username, old)) { etOldPass.error = "Wrong password"; return }
        if (new.length < 6) { etNewPass.error = "Min 6 characters"; return }
        if (new != confirm) { etConfirmPass.error = "Passwords don't match"; return }

        if (db.updatePassword(username, new)) {
            etOldPass.text?.clear(); etNewPass.text?.clear(); etConfirmPass.text?.clear()
            toast("Password changed")
        } else toast("Update failed")
    }

    private fun logout() {
        getSharedPreferences("MiniShopPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

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
