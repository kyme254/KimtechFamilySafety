package com.kimtech.familysafety

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val apiBaseUrl =
        "https://kimtech.co.ke/parentguard/api/data/"

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var testButton: Button
    private lateinit var setupButton: Button

    private val background = Color.rgb(16, 18, 24)
    private val accent = Color.rgb(220, 38, 38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        statusText = TextView(this).apply {
            text = "Server connection has not been tested."
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 20)
        }

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        testButton = Button(this).apply {
            text = "Test Server Connection"
            setOnClickListener {
                testServerConnection()
            }
        }

        setupButton = Button(this).apply {
            text = "Begin Device Setup"
            setOnClickListener {
                statusText.text =
                    "Device enrollment is not implemented yet. " +
                    "Complete authorized setup before enabling management."
            }
        }

        val information = TextView(this).apply {
            text = """
                WELCOME TO PARENTGUARD

                This app connects to the Kimtech Family Safety service.

                SERVER ADDRESS
                $apiBaseUrl

                CURRENT FEATURES
                • Server connection testing
                • Connection status display

                NEXT DEVELOPMENT STEPS
                • Authorized child-device registration
                • Parent-approved consent
                • Secure device authentication
                • Server synchronization
                • Supported Android Device Owner enrollment

                Device management is not active in this version.
            """.trimIndent()

            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 24, 0, 12)
        }

        content.addView(title)
        content.addView(subtitle)
        content.addView(description)
        content.addView(statusText)

        content.addView(
            progressBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                bottomMargin = 12
            }
        )

        content.addView(
            testButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

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

    private fun testServerConnection() {
        testButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        statusText.text = "Connecting to ParentGuard server..."
        statusText.setTextColor(Color.LTGRAY)

        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                connection = (URL(apiBaseUrl + "auth.php")
                    .openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("Accept", "application/json")
                }

                val responseCode = connection.responseCode

                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = stream?.bufferedReader()?.use {
                    it.readText()
                }.orEmpty()

                val json = try {
                    JSONObject(response)
                } catch (_: Exception) {
                    JSONObject()
                }

                val serverMessage = json.optString(
                    "message",
                    json.optString("error", "")
                )

                mainHandler.post {
                    if (responseCode in 200..299) {
                        statusText.text =
                            "Server endpoint reachable (HTTP $responseCode).\n" +
                            if (serverMessage.isNotBlank()) {
                                serverMessage
                            } else {
                                "Reachability confirmed; authentication is not verified."
                            }

                        statusText.setTextColor(
                            Color.rgb(74, 222, 128)
                        )
                    } else {
                        statusText.text =
                            "Server responded with HTTP $responseCode.\n" +
                            serverMessage

                        statusText.setTextColor(
                            Color.rgb(248, 113, 113)
                        )
                    }

                    testButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                mainHandler.post {
                    statusText.text =
                        "Connection failed: " +
                        (e.message ?: "Unable to reach the server.")

                    statusText.setTextColor(
                        Color.rgb(248, 113, 113)
                    )

                    testButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
