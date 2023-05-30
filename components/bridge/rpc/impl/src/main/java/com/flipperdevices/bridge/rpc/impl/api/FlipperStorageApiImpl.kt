package com.flipperdevices.bridge.rpc.impl.api

import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.rpc.impl.delegates.MkDirDelegate
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperStorageApi::class)
class FlipperStorageApiImpl @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val mkDirDelegate: MkDirDelegate
) : FlipperStorageApi {
    override suspend fun mkdirs(path: String) {
        mkDirDelegate.mkdir(flipperServiceProvider.getServiceApi().requestApi, path)
    }
}