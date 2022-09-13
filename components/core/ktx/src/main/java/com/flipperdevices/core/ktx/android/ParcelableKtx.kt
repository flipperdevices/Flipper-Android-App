package com.flipperdevices.core.ktx.android

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

fun Parcelable.toBytes(): ByteArray = Parcel.obtain().use {
    writeParcelable(this@toBytes, 0)
    marshall()
}

inline fun <reified T> fromBytes(bytes: ByteArray): T? where T : Parcelable {
    return Parcel.obtain().use {
        unmarshall(bytes, 0, bytes.size)
        setDataPosition(0)
        if (Build.VERSION.SDK_INT >= 33) {
            readParcelable(T::class.java.classLoader, T::class.java)
        } else readParcelable(T::class.java.classLoader)
    }
}

inline fun <reified T> Parcel.use(action: Parcel.() -> T): T {
    return try {
        action()
    } finally {
        recycle()
    }
}
