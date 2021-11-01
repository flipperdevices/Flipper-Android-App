package com.flipperdevices.pair.impl.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.FlipperSharedPreferences
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.pair.api.PairScreenArgument
import com.flipperdevices.pair.impl.PairScreenActivity
import com.flipperdevices.pair.impl.navigation.storage.PairStateStorage
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class PairComponentApiImpl @Inject constructor(
    private val preferences: FlipperSharedPreferences,
    private val pairStateStorage: PairStateStorage
) : PairComponentApi {
    override fun shouldWeOpenPairScreen(): Boolean {
        return pairStateStorage.getSavedPairState().isAllTrue().not()
    }

    override fun getPairedDevice(): String {
        val deviceId = preferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null)
        check(deviceId != null) { "Please, check isAtLeastOneTimePaired before " }
        return deviceId
    }

    override fun openPairScreen(context: Context, vararg args: PairScreenArgument) {
        context.startActivity(
            PairScreenActivity.getLaunchIntent(context, *args).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }
}
