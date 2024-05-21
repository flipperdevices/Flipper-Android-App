package com.flipperdevices.ui.decompose.util

import com.flipperdevices.ui.decompose.AndroidWindowDecorator

internal actual fun createWindowDecorator(
    statusBarIconStyleProvider: StatusBarIconStyleProvider
): WindowDecorator = AndroidWindowDecorator(statusBarIconStyleProvider)
