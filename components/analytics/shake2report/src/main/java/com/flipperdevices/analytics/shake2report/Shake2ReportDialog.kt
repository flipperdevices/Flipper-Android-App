package com.flipperdevices.analytics.shake2report

import android.content.Context
import androidx.appcompat.app.AlertDialog

object Shake2ReportDialog {
    fun show(context: Context) {
        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.shake2report_dialog_title)
            .setNegativeButton(R.string.shake2report_dialog_cancel) { _, _ ->
                dialog.cancel()
            }.setPositiveButton(R.string.shake2report_dialog_ok) { _, _ ->
                dialog.cancel()
            }.create()

        dialog.show()
    }
}
