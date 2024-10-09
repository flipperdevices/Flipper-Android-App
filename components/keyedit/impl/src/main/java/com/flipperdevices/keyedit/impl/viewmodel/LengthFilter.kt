package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val MAX_NAME_LENGTH = 21
private const val MAX_NOTE_LENGTH = 1024
private const val VIBRATOR_TIME_MS = 500L

class LengthFilter(
    context: Context,
    private val dataStoreSettings: DataStore<Settings>,
) {
    private val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    fun nameLengthFilter(name: String, result: (String) -> Unit) {
        maxLengthFilter(name, MAX_NAME_LENGTH, result)
    }

    fun noteLengthFilter(note: String, result: (String) -> Unit) {
        maxLengthFilter(note, MAX_NOTE_LENGTH, result)
    }

    private fun maxLengthFilter(
        source: String,
        maxLength: Int,
        result: (String) -> Unit
    ) {
        var newLine = source
        if (source.length > maxLength) {
            newLine = newLine.substring(0, maxLength)
            vibrator?.vibrateCompat(
                VIBRATOR_TIME_MS,
                runBlocking { dataStoreSettings.data.first().disabled_vibration }
            )
        }

        result(newLine)
    }
}
