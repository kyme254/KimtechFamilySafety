package ke.co.kimtech.gateway

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var serverUrl: EditText
    private lateinit var deviceToken: EditText
    private lateinit var statusText: TextView

    companion object {
        private const val SMS_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(Color.rgb(245, 247, 250))
        }

        val scrollView = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "KIMTECH"
            textSize = 30f
            setTextColor(Color.rgb(13, 71, 161))
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "ANDROID SMS GATEWAY"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            text = "Status: Not configured"
            textSize = 16f
            setTextColor(Color.rgb(70, 70, 70))
            setPadding(0, 24, 0, 20)
        }

        serverUrl = EditText(this).apply {
            hint = "Server URL"
            setSingleLine(true)
            setText("https://kimtech.co.ke/")
        }

        deviceToken = EditText(this).apply {
            hint = "Device enrollment token"
            setSingleLine(true)
        }

        val permissionButton = Button(this).apply {
            text = "Check SMS Permissions"
            setOnClickListener {
                checkSmsPermissions()
            }
        }

        val saveButton = Button(this).apply {
            text = "Save Configuration"
            setOnClickListener {
                saveConfiguration()
            }
        }

        val info = TextView(this).apply {
            text = """
                Setup instructions

                1. Enter your configured server URL.
                2. Enter the device token issued by your server.
                3. Save your configuration.
                4. Check SMS permissions.

                Note: This screen does not send SMS or connect
                to the server yet. The server API and gateway
                worker must be integrated and tested separately.
            """.trimIndent()

            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 20, 0, 12)
        }

        content.addView(title)
        content.addView(subtitle)
        content.addView(statusText)
        content.addView(serverUrl)
        content.addView(deviceToken)
        content.addView(saveButton)
        content.addView(permissionButton)
        content.addView(info)

        scrollView.addView(content)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
        loadConfiguration()
    }

    private fun saveConfiguration() {
        val url = serverUrl.text.toString().trim()
        val token = deviceToken.text.toString().trim()

        if (!url.startsWith("https://")) {
            Toast.makeText(
                this,
                "Enter a valid HTTPS server URL",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (token.isBlank()) {
            Toast.makeText(
                this,
                "Enter your device enrollment token",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        getSharedPreferences("kimtech_gateway", MODE_PRIVATE)
            .edit()
            .putString("server_url", url)
            .putString("device_token", token)
            .apply()

        statusText.text = "Status: Configuration saved"

        Toast.makeText(
            this,
            "Configuration saved on this device",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadConfiguration() {
        val prefs = getSharedPreferences(
            "kimtech_gateway",
            MODE_PRIVATE
        )

        serverUrl.setText(
            prefs.getString(
                "server_url",
                "https://kimtech.co.ke/"
            )
        )

        deviceToken.setText(
            prefs.getString("device_token", "")
        )
    }

    private fun checkSmsPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            statusText.text = "Status: Check device SMS support"
            return
        }

        val permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        val missing = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            statusText.text = "Status: SMS permissions granted"

            Toast.makeText(
                this,
                "SMS permissions are granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestPermissions(
                missing.toTypedArray(),
                SMS_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == SMS_PERMISSION_REQUEST) {
            val allGranted = grantResults.isNotEmpty() &&
                grantResults.all {
                    it == PackageManager.PERMISSION_GRANTED
                }

            statusText.text = if (allGranted) {
                "Status: SMS permissions granted"
            } else {
                "Status: Some SMS permissions were denied"
            }
        }
    }
}
