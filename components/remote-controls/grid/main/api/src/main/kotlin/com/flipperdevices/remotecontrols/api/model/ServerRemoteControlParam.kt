package com.flipperdevices.remotecontrols.api.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.UI_INFRARED_EXTENSION

data class ServerRemoteControlParam(val infraredFileId: Long) {

    val key: String
        get() = this.toString()

    val extTempFolderPath: String
        get() = "${FlipperKeyType.INFRARED.flipperDir}/temp/"

    val nameWithExtension: String
        get() = "$infraredFileId.ir"

    val uiFileNameWithExtension: String
        get() = "$infraredFileId.$UI_INFRARED_EXTENSION"
}
