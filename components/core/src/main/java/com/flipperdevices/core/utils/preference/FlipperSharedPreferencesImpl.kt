package com.flipperdevices.core.utils.preference

import android.content.Context
import android.content.SharedPreferences

private const val KEY_WARMUP = "warmup"

class FlipperSharedPreferencesImpl(
    private val context: Context
) : FlipperSharedPreferences,
    SharedPreferences by context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE) {

    override fun warmUp() {
        getBoolean(KEY_WARMUP, true)
    }
}
