package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val image = findViewById<ImageView>(R.id.back_icon)
        image.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        val settingsShare = findViewById<FrameLayout>(R.id.settingsShare)
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
            val support = Intent(Intent.ACTION_SENDTO)
            support.data = Uri.parse("mailto:")
            support.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
            support.putExtra(Intent.EXTRA_SUBJECT, arrayOf(getString(R.string.subject)))
            support.putExtra(Intent.EXTRA_TEXT, message)
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