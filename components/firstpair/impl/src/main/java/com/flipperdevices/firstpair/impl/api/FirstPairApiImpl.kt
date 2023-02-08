package com.flipperdevices.firstpair.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class FirstPairApiImpl @Inject constructor(
    private val firstPairStorage: FirstPairStorage
) : FirstPairApi {
    override fun shouldWeOpenPairScreen(): Boolean {
        return firstPairStorage.isTosPassed().not() || firstPairStorage.isDeviceSelected().not()
    }
}
