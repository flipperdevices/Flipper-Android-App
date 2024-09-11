package com.flipperdevices.ui.decompose.internal

import com.flipperdevices.ui.decompose.AndroidWindowDecorator
import com.flipperdevices.ui.decompose.statusbar.StatusBarIconStyleProvider

internal actual fun createWindowDecorator(
    statusBarIconStyleProvider: StatusBarIconStyleProvider
): WindowDecorator = AndroidWindowDecorator(statusBarIconStyleProvider)
