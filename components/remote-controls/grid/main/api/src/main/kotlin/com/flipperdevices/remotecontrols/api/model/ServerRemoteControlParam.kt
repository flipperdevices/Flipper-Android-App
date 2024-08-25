package com.flipperdevices.remotecontrols.api.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.UI_INFRARED_EXTENSION

data class ServerRemoteControlParam(
    val infraredFileId: Long,
    val remoteName: String
) {

    val key: String
        get() = this.toString()

    val extTempFolderPath: String
        get() = "${FlipperKeyType.INFRARED.flipperDir}/temp/"

    val nameWithExtension: String
        get() = "$remoteName.ir"

    val uiFileNameWithExtension: String
        get() = "$remoteName.$UI_INFRARED_EXTENSION"
}
