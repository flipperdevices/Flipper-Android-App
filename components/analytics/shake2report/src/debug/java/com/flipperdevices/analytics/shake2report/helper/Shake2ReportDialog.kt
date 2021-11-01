package com.flipperdevices.analytics.shake2report.helper

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.flipperdevices.analytics.shake2report.R
import com.flipperdevices.analytics.shake2report.databinding.DialogEdittextBinding

object Shake2ReportDialog {
    fun show(activity: Activity, onCancel: () -> Unit, onSuccess: (String) -> Unit) {
        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        val editText = DialogEdittextBinding.inflate(activity.layoutInflater)

        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.shake2report_dialog_title)
            .setView(editText.root)
            .setNegativeButton(R.string.shake2report_dialog_cancel) { _, _ ->
                onCancel()
                dialog.cancel()
            }.setPositiveButton(R.string.shake2report_dialog_ok) { _, _ ->
                onSuccess(editText.edittext.text.toString())
                dialog.cancel()
            }.create()

        dialog.show()
    }
}
