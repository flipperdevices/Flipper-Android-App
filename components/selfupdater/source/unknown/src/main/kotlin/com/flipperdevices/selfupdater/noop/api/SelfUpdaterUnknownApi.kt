package com.flipperdevices.selfupdater.noop.api

import android.app.Activity
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterUnknownApi @Inject constructor() : SelfUpdaterApi {
    override fun startCheckUpdateAsync(activity: Activity) = Unit

    override fun getInstallSourceName() = "Unknown"
}
