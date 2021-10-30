package com.flipperdevices.analytics.shake2report.helper

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.flipperdevices.analytics.shake2report.R

object Shake2ReportDialog {
    fun show(activity: Activity, onCancel: () -> Unit, onSuccess: () -> Unit) {
        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.shake2report_dialog_title)
            .setNegativeButton(R.string.shake2report_dialog_cancel) { _, _ ->
                onCancel()
                dialog.cancel()
            }.setPositiveButton(R.string.shake2report_dialog_ok) { _, _ ->
                onSuccess()
                dialog.cancel()
            }.create()

        dialog.show()
    }
}
