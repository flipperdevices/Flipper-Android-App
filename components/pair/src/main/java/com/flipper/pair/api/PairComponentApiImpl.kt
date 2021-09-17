package com.flipper.pair.api

import android.content.Context
import android.content.Intent
import com.flipper.core.api.PairComponentApi
import com.flipper.core.di.AppGraph
import com.flipper.core.utils.preference.FlipperSharedPreferences
import com.flipper.core.utils.preference.FlipperSharedPreferencesKey
import com.flipper.pair.PairScreenActivity
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class PairComponentApiImpl @Inject constructor(
    private val preferences: FlipperSharedPreferences
) : PairComponentApi {
    override fun isAtLeastOneTimePaired(): Boolean {
        return preferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null) != null
    }

    override fun getPairedDevice(): String {
        val deviceId = preferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null)
        check(deviceId != null) { "Please, check isAtLeastOneTimePaired before " }
        return deviceId
    }

    override fun openPairScreen(context: Context) {
        context.startActivity(
            Intent(context, PairScreenActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }
}
