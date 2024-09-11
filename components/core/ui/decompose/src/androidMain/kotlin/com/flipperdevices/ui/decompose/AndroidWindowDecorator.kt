package com.flipperdevices.ui.decompose

import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.ui.decompose.internal.WindowDecorator
import com.flipperdevices.ui.decompose.statusbar.StatusBarIconStyleProvider

internal class AndroidWindowDecorator(
    private val statusBarIconStyleProvider: StatusBarIconStyleProvider
) : WindowDecorator {

    @Suppress("Deprecated")
    private fun changeStatusBarContrastStyle(decor: View, isLightIcons: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = decor.windowInsetsController ?: return
            windowInsetsController.setSystemBarsAppearance(
                if (isLightIcons) {
                    0
                } else {
                    APPEARANCE_LIGHT_STATUS_BARS
                },
                APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            if (isLightIcons) {
                decor.systemUiVisibility =
                    decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                decor.systemUiVisibility =
                    decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun decorate() {
        val activity = CurrentActivityHolder.getCurrentActivity() ?: return
        val decor = activity.window?.decorView ?: return
        val uiMode = activity.resources.configuration.uiMode
        val systemIsDark =
            (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        changeStatusBarContrastStyle(
            decor,
            statusBarIconStyleProvider.isStatusBarIconLight(systemIsDark)
        )
    }
}
