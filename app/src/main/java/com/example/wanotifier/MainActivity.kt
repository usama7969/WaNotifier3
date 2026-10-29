package com.example.wanotifier

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
        tts = TextToSpeech(this) { if (it == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault() }

        val pad = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        fun label(t: String) = TextView(this).apply { text = t; setPadding(0, pad / 2, 0, 0) }

        root.addView(label("WhatsApp message aane par aawaz mein bataye ga ke kis ka message aaya hai."))

        root.addView(label("Step 1: Notification access ON karein (WA Notifier ko allow karein)"))
        root.addView(Button(this).apply {
            text = "Open Notification Access settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
        })

        root.addView(label("Step 2: Option"))
        val readText = CheckBox(this).apply {
            text = "Message ka text bhi parh kar sunao"
            isChecked = prefs.getBoolean("readText", false)
            setOnCheckedChangeListener { _, c -> prefs.edit().putBoolean("readText", c).apply() }
        }
        root.addView(readText)

        root.addView(label("Step 3: Test"))
        root.addView(Button(this).apply {
            text = "Test aawaz"
            setOnClickListener { tts?.speak("Ali ka WhatsApp message aaya hai", TextToSpeech.QUEUE_FLUSH, null, "test") }
        })

        root.addView(label("Note: aawaz phone ki media volume par aati hai, volume on rakhein."))
        setContentView(ScrollView(this).apply { addView(root) })
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
