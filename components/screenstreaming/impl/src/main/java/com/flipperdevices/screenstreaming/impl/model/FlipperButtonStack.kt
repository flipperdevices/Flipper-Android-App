package com.flipperdevices.screenstreaming.impl.model

import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import java.util.UUID

data class FlipperButtonStack(
    val enum: ButtonEnum,
    val uuid: UUID = UUID.randomUUID(),
)
