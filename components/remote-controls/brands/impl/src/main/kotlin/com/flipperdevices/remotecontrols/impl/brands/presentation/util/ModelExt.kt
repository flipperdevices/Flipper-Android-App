package com.flipperdevices.remotecontrols.impl.brands.presentation.util

import com.flipperdevices.ifrmvp.backend.model.BrandModel

fun BrandModel.charSection(): Char {
    val ch = name.first()
    return when {
        ch.isDigit() -> '#'
        ch.isLetter() -> ch.uppercaseChar()
        else -> '#'
    }
}
