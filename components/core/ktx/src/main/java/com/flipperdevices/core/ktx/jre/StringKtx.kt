package com.flipperdevices.core.ktx.jre

import java.util.Locale

fun String.titlecaseFirstCharIfItIsLowercase() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
