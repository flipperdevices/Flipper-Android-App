package com.flipperdevices.inappnotification.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InAppNotificationRendererImpl @Inject constructor() : InAppNotificationRenderer
