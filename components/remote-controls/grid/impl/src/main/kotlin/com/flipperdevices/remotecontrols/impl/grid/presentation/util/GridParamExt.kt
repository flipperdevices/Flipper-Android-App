package com.flipperdevices.remotecontrols.impl.grid.presentation.util

import android.util.Log
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent

object GridParamExt {
    val GridScreenDecomposeComponent.Param.extFolderPath: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "${FlipperKeyType.INFRARED.flipperDir}/temp/"
            is GridScreenDecomposeComponent.Param.Path -> "/${flipperKeyPath.path.folder}"
        }.also { Log.d("MAKEEVRSERG", "fullFolderName: $it") }

    val GridScreenDecomposeComponent.Param.nameWithExtension: String
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> "$irFileId.ir"
            is GridScreenDecomposeComponent.Param.Path -> flipperKeyPath.path.nameWithExtension
        }.also { Log.d("MAKEEVRSERG", "nameWithExtension: $it") }

    val GridScreenDecomposeComponent.Param.flipperFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extFolderPath,
            nameWithExtension = nameWithExtension
        ).also { Log.d("MAKEEVRSERG", "flipperFilePath: $it") }

    val GridScreenDecomposeComponent.Param.irFileIdOrNull: Long?
        get() = when (this) {
            is GridScreenDecomposeComponent.Param.Id -> irFileId
            is GridScreenDecomposeComponent.Param.Path -> null
        }.also { Log.d("MAKEEVRSERG", "irFileIdOrNull: $it") }
}
