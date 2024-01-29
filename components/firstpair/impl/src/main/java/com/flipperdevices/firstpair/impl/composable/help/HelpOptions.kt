package com.flipperdevices.firstpair.impl.composable.help

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import com.flipperdevices.core.log.error
import com.flipperdevices.firstpair.impl.R

sealed class HelpOptions(
    @StringRes val title: Int,
    @StringRes val description: Int?
) {
    companion object {
        val values = listOf(
            CheckName,
            BluetoothOnFlipper,
            BtConnection,
            DisconnectFlipper,
            UpdateFlipper,
            UpdateApp,
            RebootFlipper
        )
    }

    object DisconnectFlipper : HelpOptions(
        R.string.firstpair_help_4_title,
        null
    )

    abstract class MarkdownHelpOptions(
        @StringRes title: Int,
        @StringRes description: Int?
    ) : HelpOptions(title, description)

    object CheckName : MarkdownHelpOptions(
        R.string.firstpair_help_1_title,
        R.string.firstpair_help_1_description
    )

    object BluetoothOnFlipper : MarkdownHelpOptions(
        R.string.firstpair_help_2_title,
        R.string.firstpair_help_2_description
    )

    object UpdateFlipper : MarkdownHelpOptions(
        R.string.firstpair_help_5_title,
        R.string.firstpair_help_5_description
    )

    object RebootFlipper : MarkdownHelpOptions(
        R.string.firstpair_help_7_title,
        R.string.firstpair_help_7_description
    )

    abstract class CustomOpenLinkHandler(
        @StringRes title: Int,
        @StringRes description: Int?
    ) : HelpOptions(title, description) {
        abstract fun onClick(context: Context)
    }

    object BtConnection : CustomOpenLinkHandler(
        R.string.firstpair_help_3_title,
        R.string.firstpair_help_3_description
    ) {
        override fun onClick(context: Context) {
            context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        }
    }

    object UpdateApp : CustomOpenLinkHandler(
        R.string.firstpair_help_6_title,
        R.string.firstpair_help_6_description
    ) {
        override fun onClick(context: Context) {
            val packageName = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                error(e) { "Failed open play store, try open browser" }
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }
}
