package com.flipperdevices.pair.impl.navigation.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.flipperdevices.bridge.api.utils.DeviceFeatureHelper
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import com.flipperdevices.pair.impl.navigation.models.PairScreenState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val KEY_TOS_PASS = "pair_tos_pass"

@ContributesBinding(AppGraph::class)
class PairStateStorageImpl @Inject constructor(
    private val pref: SharedPreferences,
    private val context: Context
) : PairStateStorage {
    override fun getSavedPairState(): PairScreenState {
        val tosAccepted = pref.getBoolean(KEY_TOS_PASS, false)
        val devicePaired = pref.getString(FlipperSharedPreferencesKey.DEVICE_ID, null) != null
        var permissionGranted = DeviceFeatureHelper.isCompanionFeatureAvailable(context)
        if (!permissionGranted) {
            permissionGranted = PermissionHelper.isPermissionGranted(context)
        }
        return PairScreenState(
            tosAccepted = tosAccepted,
            devicePaired = devicePaired,
            permissionGranted = permissionGranted,
            guidePassed = devicePaired
        )
    }

    override fun markTosPassed() {
        pref.edit { putBoolean(KEY_TOS_PASS, true) }
    }
}
