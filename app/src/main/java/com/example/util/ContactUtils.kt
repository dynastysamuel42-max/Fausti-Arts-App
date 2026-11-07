package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ContactUtils {
    const val OFFICIAL_EMAIL = "faustiarts1@gmail.com"
    const val OFFICIAL_PHONE = "+211922470980"
    const val OFFICIAL_WHATSAPP_URL = "https://wa.me/211922470980"
    const val OFFICIAL_WEBSITE_URL = "https://fausti-art-hub.base44.app"

    fun openEmail(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$OFFICIAL_EMAIL")
            }
            context.startActivity(Intent.createChooser(intent, "Email Fausti Arts"))
        } catch (_: Exception) {
            Toast.makeText(context, "Contact: $OFFICIAL_EMAIL", Toast.LENGTH_LONG).show()
        }
    }

    fun openPhoneDialer(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$OFFICIAL_PHONE")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Contact: $OFFICIAL_PHONE", Toast.LENGTH_LONG).show()
        }
    }

    fun openWhatsApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WHATSAPP_URL))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "WhatsApp: $OFFICIAL_PHONE", Toast.LENGTH_LONG).show()
        }
    }

    fun openWebsite(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(OFFICIAL_WEBSITE_URL))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Website: $OFFICIAL_WEBSITE_URL", Toast.LENGTH_LONG).show()
        }
    }
}
