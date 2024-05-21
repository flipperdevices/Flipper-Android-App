package com.flipperdevices.ui.decompose.internal

internal actual fun createWindowDecorator(
    statusBarIconStyleProvider: StatusBarIconStyleProvider
): WindowDecorator = DesktopWindowDecorator()
