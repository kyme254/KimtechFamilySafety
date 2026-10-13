package com.kimtech.familysafety

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val apiBaseUrl =
        "https://kimtech.co.ke/parentguard/api/data/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val background = Color.rgb(16, 18, 24)
        val accent = Color.rgb(220, 38, 38)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 28)
            setBackgroundColor(background)
        }

        val title = TextView(this).apply {
            text = "KIMTECH"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "FAMILY SAFETY"
            textSize = 20f
            setTextColor(accent)
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "ParentGuard Child Companion"
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 16)
        }

        val status = TextView(this).apply {
            text = "Device setup required"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 24)
        }

        val setupButton = Button(this).apply {
            text = "Begin Device Setup"
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Continue setup through your authorized parent account.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val information = TextView(this).apply {
            text = """
                Welcome to ParentGuard.

                This app is designed to work with the
                Kimtech Family Safety service.

                Device monitoring must be configured
                transparently with the appropriate consent
                and Android permissions.

                API configuration:
                $apiBaseUrl

                Connection status: Not yet tested
            """.trimIndent()

            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 24, 0, 12)
        }

        content.addView(title)
        content.addView(subtitle)
        content.addView(description)
        content.addView(status)

        content.addView(
            setupButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(information)

        val scrollView = ScrollView(this).apply {
            addView(content)
        }

        setContentView(scrollView)
    }
}
