package com.flipperdevices.ui.decompose.util

internal actual fun createWindowDecorator(
    statusBarIconStyleProvider: StatusBarIconStyleProvider
): WindowDecorator = DesktopWindowDecorator()
