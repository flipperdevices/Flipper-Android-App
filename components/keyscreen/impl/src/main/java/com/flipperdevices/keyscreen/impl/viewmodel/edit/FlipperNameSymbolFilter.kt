package com.flipperdevices.keyscreen.impl.viewmodel.edit

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.flipperdevices.core.ktx.android.vibrateCompat

private const val VIBRATOR_TIME_MS = 500L

// http://elm-chan.org/fsw/ff/pf/filename.html
private const val ALLOWED_CHARS =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#\$%&'()-@^_`{}~"

class FlipperSymbolFilter(context: Context) {
    private var vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    fun filterUnacceptableSymbol(text: String, block: (String) -> Unit) {
        val filteredString = text.map { if (it == ' ') '_' else it }
            .filter { ALLOWED_CHARS.contains(it) }
            .toCharArray().let { String(it) }

        if (filteredString.length != text.length) {
            // String contains forbidden characters
            vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
        }

        block(filteredString)
    }
}
