package com.practicum.playlistmaker.settings.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R

class SettingsRouter(private val context: Context) : ISettingsRouter {
    private val extraMail = context.getText(R.string.mail)
    private val subject = context.getText(R.string.subject)
    private val message = context.getText(R.string.message)
    private val share = context.getText(R.string.share)

    override fun shareLink(shareUrl: String) {
        val intent = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareUrl)
            type = "text/plain"
        }, share)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun openEmail() {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(extraMail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
