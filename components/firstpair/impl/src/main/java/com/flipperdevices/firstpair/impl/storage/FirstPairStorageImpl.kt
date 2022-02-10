package com.flipperdevices.firstpair.impl.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val KEY_TOS_PASS = "pair_tos_pass_v2"
private const val KEY_DEVICE_PASS = "pair_device_pass"

@ContributesBinding(AppGraph::class)
class FirstPairStorageImpl @Inject constructor(
    private val prefs: SharedPreferences
) : FirstPairStorage {
    override fun isTosPassed(): Boolean {
        return prefs.getBoolean(KEY_TOS_PASS, false)
    }

    override fun isDeviceSelected(): Boolean {
        return prefs.getBoolean(KEY_DEVICE_PASS, false)
    }

    override fun markTosPassed() {
        prefs.edit { putBoolean(KEY_TOS_PASS, true) }
    }

    override fun markDeviceSelected(deviceId: String?) {
        prefs.edit {
            putBoolean(KEY_DEVICE_PASS, true)
            if (deviceId != null) {
                putString(FlipperSharedPreferencesKey.DEVICE_ID, deviceId)
            }
        }
    }
}
