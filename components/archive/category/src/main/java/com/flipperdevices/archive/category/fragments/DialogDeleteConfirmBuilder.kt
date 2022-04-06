package com.flipperdevices.archive.category.fragments

import androidx.appcompat.app.AlertDialog
import com.flipperdevices.archive.category.R
import com.flipperdevices.archive.category.viewmodels.DeleteViewModel
import com.flipperdevices.core.activityholder.CurrentActivityHolder

object DialogDeleteConfirmBuilder {
    fun show(deleteViewModel: DeleteViewModel) {
        val activity = CurrentActivityHolder.getCurrentActivity() ?: return

        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activity)
            .setMessage(R.string.dialog_delete_all_text)
            .setPositiveButton(R.string.dialog_delete_all_confirm) { _, _ ->
                deleteViewModel.onDeleteAll(force = true)
                dialog.cancel()
            }
            .setNeutralButton(R.string.dialog_delete_all_cancel) { _, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }
}
