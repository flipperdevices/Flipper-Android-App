package com.flipperdevices.nfc.attack.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.attack.api.NfcAttackApi
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(AppGraph::class, NfcAttackApi::class)
class NfcAttackApiImpl @Inject constructor(
    private val mfKey32Api: MfKey32Api
) : NfcAttackApi {
    override fun notificationCount(): Flow<Int> = mfKey32Api.hasNotification().map {
        if (it) 1 else 0
    }
}
