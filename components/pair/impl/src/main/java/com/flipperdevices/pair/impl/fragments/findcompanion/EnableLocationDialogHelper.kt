package com.flipperdevices.pair.impl.fragments.findcompanion

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.flipperdevices.pair.impl.R

object EnableLocationDialogHelper {
    fun showDialogIfLocationDisabled(context: Context) {
        val locationManager =
            ContextCompat.getSystemService(context, LocationManager::class.java) ?: return

        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
        if (isLocationEnabled) {
            return
        }

        @Suppress("JoinDeclarationAndAssignment")
        lateinit var dialog: AlertDialog

        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.pair_companion_enable_location_title)
            .setMessage(R.string.pair_companion_enable_location_desc)
            .setCancelable(false)
            .setNeutralButton(R.string.pair_companion_enable_location_btn_more) { _, _ ->
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            context.getString(R.string.pair_companion_enable_location_btn_more_link)
                        )
                    )
                )
                dialog.cancel()
            }
            .setPositiveButton(R.string.pair_companion_enable_location_btn_open_settings) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.cancel()
            }.create()

        dialog.show()
    }
}
