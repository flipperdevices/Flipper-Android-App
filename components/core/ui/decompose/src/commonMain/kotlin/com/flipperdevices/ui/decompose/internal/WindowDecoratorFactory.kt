package com.flipperdevices.ui.decompose.internal

import com.flipperdevices.ui.decompose.statusbar.StatusBarIconStyleProvider

internal expect fun createWindowDecorator(
    statusBarIconStyleProvider: StatusBarIconStyleProvider
): WindowDecorator
