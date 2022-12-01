package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.core.data.SemVer
import kotlinx.coroutines.flow.StateFlow

interface FlipperVersionApi {
    fun getVersionInformationFlow(): StateFlow<SemVer?>
}
