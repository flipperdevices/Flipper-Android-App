package com.flipperdevices.core.ktx.android

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable

fun Intent.toFullString(): String {
    val sb = StringBuilder()

    sb.append(toString())

    val localExtras = extras ?: return sb.toString()

    sb.append(". Extras{")
    localExtras.keySet().map {
        @Suppress("DEPRECATION")
        it to localExtras.get(it)
    }.joinTo(buffer = sb, separator = ",") { "${it.first}=${it.second}" }
    sb.append('}')

    return sb.toString()
}

inline fun <reified T : Parcelable> Intent.parcelableExtra(key: String): T? {
    return when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getParcelableExtra(key, T::class.java)
        }
        else -> {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }
}
