package com.kimtech.familysafety

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ParentGuardDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(
        context: Context,
        intent: Intent
    ) {
        super.onEnabled(context, intent)

        Toast.makeText(
            context,
            "ParentGuard device administrator enabled.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDisabled(
        context: Context,
        intent: Intent
    ) {
        super.onDisabled(context, intent)

        Toast.makeText(
            context,
            "ParentGuard device administrator disabled.",
            Toast.LENGTH_LONG
        ).show()
    }
}
