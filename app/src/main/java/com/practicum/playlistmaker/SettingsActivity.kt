package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val image = findViewById<ImageView>(R.id.back_icon)
        image.setOnClickListener {
            finish()
        }
        val settingsShare = findViewById<FrameLayout>(R.id.settingsShare)
        val switchThemeMode = findViewById<SwitchMaterial>(R.id.switch_theme_mode)
        switchThemeMode.isChecked = getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(
            SWITCH_PREFERENCES_KEY,false)
        switchThemeMode.setOnCheckedChangeListener { button, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            (applicationContext as App).saveTheme(isChecked)
        }

        settingsShare.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_yandex))
            val share = Intent(Intent.ACTION_SEND)
            share.putExtra(Intent.EXTRA_TEXT, url)
            share.type = "text/plain"
            startActivity(share)
        }
        val settingsSuport = findViewById<FrameLayout>(R.id.settingsSuport)
        settingsSuport.setOnClickListener {
            val message = getString(R.string.message)
            val support = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(support)
        }
        val userAgreement = findViewById<FrameLayout>(R.id.userAgreement)
        userAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.agreement))
            val userAgr = Intent(Intent.ACTION_VIEW, url)
            startActivity(userAgr)
        }
    }
}