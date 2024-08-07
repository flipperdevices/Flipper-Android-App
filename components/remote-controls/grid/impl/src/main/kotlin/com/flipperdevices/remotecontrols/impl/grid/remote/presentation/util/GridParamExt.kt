package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.util

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.remotecontrols.api.model.GridControlParam

object GridParamExt {
    val GridControlParam.extTempFolderPath: String
        get() = when (this) {
            is GridControlParam.Id -> "${FlipperKeyType.INFRARED.flipperDir}/temp/"
            is GridControlParam.Path -> "/${flipperKeyPath.path.folder}"
        }

    val GridControlParam.extFolderPath: String
        get() = when (this) {
            is GridControlParam.Id -> "${FlipperKeyType.INFRARED.flipperDir}/"
            is GridControlParam.Path -> "/${flipperKeyPath.path.folder}"
        }

    val GridControlParam.nameWithExtension: String
        get() = when (this) {
            is GridControlParam.Id -> "$irFileId.ir"
            is GridControlParam.Path -> flipperKeyPath.path.nameWithExtension
        }

    val GridControlParam.uiFileNameWithExtension: String
        get() = when (this) {
            is GridControlParam.Id -> "$irFileId.ui.json"
            is GridControlParam.Path ->
                flipperKeyPath.path.nameWithExtension
                    .replace(".ir", "")
                    .plus(".ui.json")
        }

    val GridControlParam.flipperTempFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extTempFolderPath,
            nameWithExtension = nameWithExtension
        )
    val GridControlParam.flipperFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extFolderPath,
            nameWithExtension = nameWithExtension
        )

    val GridControlParam.irFileIdOrNull: Long?
        get() = when (this) {
            is GridControlParam.Id -> irFileId
            is GridControlParam.Path -> null
        }
}
