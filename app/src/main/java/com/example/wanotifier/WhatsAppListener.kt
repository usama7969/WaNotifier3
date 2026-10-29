package com.example.wanotifier

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import java.util.Locale

class WhatsAppListener : NotificationListenerService(), TextToSpeech.OnInitListener {
    private val packages = setOf("com.whatsapp", "com.whatsapp.w4b")
    private var tts: TextToSpeech? = null
    private var ready = false

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            ready = true
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!ready || sbn.packageName !in packages) return
        val n = sbn.notification
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val extras = n.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        if (title == "WhatsApp" && text.contains("new messages", true)) return

        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        val readText = prefs.getBoolean("readText", false)

        val speech = if (readText) "$title ka message aaya hai. $text"
                     else "$title ka WhatsApp message aaya hai"
        tts?.speak(speech, TextToSpeech.QUEUE_ADD, null, sbn.key)
    }
}
