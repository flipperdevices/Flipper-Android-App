package com.flipperdevices.screenstreaming.impl.composable

import com.flipperdevices.protobuf.screen.Gui

enum class ButtonEnum(val key: Gui.InputKey) {
    LEFT(Gui.InputKey.LEFT),
    RIGHT(Gui.InputKey.RIGHT),
    UP(Gui.InputKey.UP),
    DOWN(Gui.InputKey.DOWN),
    OK(Gui.InputKey.OK),
    BACK(Gui.InputKey.BACK)
}
