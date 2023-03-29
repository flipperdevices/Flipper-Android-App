package com.flipperdevices.selfupdater.noop.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterUnknownApi @Inject constructor() : SelfUpdaterApi {
    override fun startCheckUpdateAsync() = Unit
}
