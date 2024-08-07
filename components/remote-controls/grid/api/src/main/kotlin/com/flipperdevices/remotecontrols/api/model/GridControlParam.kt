package com.flipperdevices.remotecontrols.api.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

sealed interface GridControlParam {
    data class Id(val irFileId: Long) : GridControlParam
    data class Path(val flipperKeyPath: FlipperKeyPath) : GridControlParam

    val key: String
        get() = this.toString()
}
