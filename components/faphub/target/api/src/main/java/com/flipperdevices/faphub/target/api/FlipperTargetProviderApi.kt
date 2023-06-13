package com.flipperdevices.faphub.target.api

import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.coroutines.flow.StateFlow

interface FlipperTargetProviderApi {
    fun getFlipperTarget(): StateFlow<FlipperTarget?>
}
