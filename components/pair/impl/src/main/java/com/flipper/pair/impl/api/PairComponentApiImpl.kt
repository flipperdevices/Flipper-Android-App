package com.flipper.pair.impl.api

import android.content.Context
import android.content.Intent
import com.flipper.core.di.AppGraph
import com.flipper.core.utils.preference.FlipperSharedPreferences
import com.flipper.core.utils.preference.FlipperSharedPreferencesKey
import com.flipper.pair.api.PairComponentApi
import com.flipper.pair.impl.PairScreenActivity
import com.flipper.pair.impl.navigation.storage.PairStateStorage
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

    override fun openPairScreen(context: Context) {
        context.startActivity(
            Intent(context, PairScreenActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }
}
