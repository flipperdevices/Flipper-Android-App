package com.flipperdevices.toolstab.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.nfc.mfkey32.api.MfKey32Api
import com.flipperdevices.toolstab.api.ToolsApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ToolsApiImpl @Inject constructor(
    private val mfKey32Api: MfKey32Api
) : ToolsApi {
    override fun hasNotification(scope: CoroutineScope): Flow<Boolean> {
        return mfKey32Api.hasNotification()
    }
}
