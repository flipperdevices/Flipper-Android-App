package com.flipperdevices.bridge.service.noop.provider

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperServiceProvider::class)
class FlipperServiceProviderNoop @Inject constructor() : FlipperServiceProvider {
    override fun provideServiceApi(
        consumer: FlipperBleServiceConsumer,
        lifecycleOwner: LifecycleOwner
    ) = Unit

    override fun provideServiceApi(
        lifecycleOwner: LifecycleOwner,
        onError: (FlipperBleServiceError) -> Unit,
        onBleManager: (FlipperServiceApi) -> Unit
    ) = Unit

    override suspend fun getServiceApi(): FlipperServiceApi {
        throw NotImplementedError()
    }
}
