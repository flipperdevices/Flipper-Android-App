package com.flipperdevices.core.ktx.android

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
        else ->
            @Suppress("DEPRECATION")
            getParcelable(key)
    }
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T {
    val value = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializable(key, T::class.java)
        else ->
            @Suppress("DEPRECATION")
            getSerializable(key) as T?
    }
    return requireNotNull(value) { "Value for serialization key $key is null" }
}
