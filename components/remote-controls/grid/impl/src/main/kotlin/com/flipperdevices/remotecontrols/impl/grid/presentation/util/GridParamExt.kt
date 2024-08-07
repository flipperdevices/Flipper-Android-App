package com.flipperdevices.remotecontrols.impl.grid.presentation.util

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent

object GridParamExt {
    val GridScreenDecomposeComponent.Param.extTempFolderPath: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "${FlipperKeyType.INFRARED.flipperDir}/temp/"
            is GridScreenDecomposeComponent.Param.Path -> "/${flipperKeyPath.path.folder}"
        }

    val GridScreenDecomposeComponent.Param.extFolderPath: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "${FlipperKeyType.INFRARED.flipperDir}/"
            is GridScreenDecomposeComponent.Param.Path -> "/${flipperKeyPath.path.folder}"
        }

    val GridScreenDecomposeComponent.Param.nameWithExtension: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "$irFileId.ir"
            is GridScreenDecomposeComponent.Param.Path -> flipperKeyPath.path.nameWithExtension
        }

    val GridScreenDecomposeComponent.Param.uiFileNameWithExtension: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "$irFileId.ui.json"
            is GridScreenDecomposeComponent.Param.Path -> flipperKeyPath.path.nameWithExtension
                .replace(".ir", "")
                .plus(".ui.json")
        }

    val GridScreenDecomposeComponent.Param.flipperTempFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extTempFolderPath,
            nameWithExtension = nameWithExtension
        )

    val GridScreenDecomposeComponent.Param.irFileIdOrNull: Long?
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> irFileId
            is GridScreenDecomposeComponent.Param.Path -> null
        }
}
