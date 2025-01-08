package com.flipperdevices.bridge.connection.feature.update.impl.api

import com.flipperdevices.bridge.connection.feature.update.api.BootApi
import com.flipperdevices.bridge.connection.feature.update.api.DisplayApi
import com.flipperdevices.bridge.connection.feature.update.api.FUpdateFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FUpdateFeatureApiImpl @AssistedInject constructor(
    @Assisted private val displayApi: DisplayApi,
    @Assisted private val bootApi: BootApi,
) : FUpdateFeatureApi,
    LogTagProvider {
    override val TAG = "FAlarmFeatureApi"

    override fun bootApi(): BootApi = bootApi

    override fun displayApi(): DisplayApi = displayApi

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            displayApi: DisplayApi,
            bootApi: BootApi,
        ): FUpdateFeatureApiImpl
    }
}
