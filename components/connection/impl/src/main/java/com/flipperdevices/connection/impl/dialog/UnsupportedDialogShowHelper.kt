package com.flipperdevices.connection.impl.dialog

import android.app.AlertDialog
import com.flipperdevices.connection.impl.R
import com.flipperdevices.core.activityholder.CurrentActivityHolder

class UnsupportedDialogShowHelper {
    private var alreadyShown = false

    fun showDialog() {
        val activityContext = CurrentActivityHolder.getCurrentActivity()
        if (alreadyShown || activityContext == null) {
            return
        }
        alreadyShown = true

        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activityContext)
            .setIcon(R.drawable.ic_warning)
            .setTitle(R.string.dialog_unsupported_title)
            .setMessage(R.string.dialog_unsupported_description)
            .setCancelable(true)
            .setNegativeButton(android.R.string.ok) { _, _ ->
                dialog.hide()
            }
            .create()
        dialog.show()
    }
}
