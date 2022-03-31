package com.flipperdevices.info.impl.fragment

import androidx.appcompat.app.AlertDialog
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.info.impl.R

object ForgetDialogApproveHelper {
    fun showDialog(flipperName: String, onForgetDevice: () -> Unit) {
        val activity = CurrentActivityHolder.getCurrentActivity() ?: return

        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.info_device_forget_dialog_title)
            .setMessage(
                activity.getString(R.string.info_device_forget_dialog_description, flipperName)
            )
            .setPositiveButton(R.string.info_device_forget_dialog_forget) { _, _ ->
                onForgetDevice()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}
