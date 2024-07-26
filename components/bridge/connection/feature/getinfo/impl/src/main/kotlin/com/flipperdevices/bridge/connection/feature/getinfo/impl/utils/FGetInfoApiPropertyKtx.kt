package com.flipperdevices.bridge.connection.feature.getinfo.impl.utils

import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty

val FGetInfoApiProperty.path
    get() = "${group}.${key}"