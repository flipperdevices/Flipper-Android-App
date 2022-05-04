package com.flipperdevices.updater.ui.fragments

import android.app.AlertDialog
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.updater.ui.R

object CancelDialogBuilder {
    fun showDialog(onConfirm: () -> Unit) {
        val activity = CurrentActivityHolder.getCurrentActivity() ?: return

        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.update_cancel_dialog_title)
            .setMessage(R.string.update_cancel_dialog_desc)
            .setPositiveButton(R.string.update_cancel_dialog_yes) { _, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.update_cancel_dialog_no) { _, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }
}
