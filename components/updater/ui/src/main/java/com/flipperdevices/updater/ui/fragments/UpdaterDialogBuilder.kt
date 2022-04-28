package com.flipperdevices.updater.ui.fragments

import android.app.AlertDialog
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.updater.model.VersionFiles
import com.flipperdevices.updater.ui.R
import com.github.terrakok.cicerone.androidx.FragmentScreen

class UpdaterDialogBuilder(
    private val globalCicerone: CiceroneGlobal
) {
    fun showDialog(versionFiles: VersionFiles?) {
        val activity = CurrentActivityHolder.getCurrentActivity() ?: return

        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.update_dialog_title)
            .setMessage(R.string.update_dialog_desc)
            .setPositiveButton(R.string.update_dialog_yes) { _, _ ->
                globalCicerone.getRouter().newRootScreen(
                    FragmentScreen {
                        UpdaterFragment.getInstance(versionFiles)
                    }
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.update_dialog_no) { _, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}
