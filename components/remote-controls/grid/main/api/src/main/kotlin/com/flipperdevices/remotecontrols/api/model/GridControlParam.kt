package com.flipperdevices.remotecontrols.api.model

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

sealed interface GridControlParam {
    data class Id(val irFileId: Long) : GridControlParam
    data class Path(val flipperKeyPath: FlipperKeyPath) : GridControlParam

    val key: String
        get() = this.toString()

    val extTempFolderPath: String
        get() = when (this) {
            is Id -> "${FlipperKeyType.INFRARED.flipperDir}/temp/"
            is Path -> "/${flipperKeyPath.path.folder}"
        }

    val extFolderPath: String
        get() = when (this) {
            is Id -> "${FlipperKeyType.INFRARED.flipperDir}/"
            is Path -> "/${flipperKeyPath.path.folder}"
        }

    val nameWithExtension: String
        get() = when (this) {
            is Id -> "$irFileId.ir"
            is Path -> flipperKeyPath.path.nameWithExtension
        }

    val uiFileNameWithExtension: String
        get() = when (this) {
            is Id -> "$irFileId.ui.json"
            is Path ->
                flipperKeyPath.path.nameWithExtension
                    .replace(".ir", "")
                    .plus(".ui.json")
        }

    val flipperTempFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extTempFolderPath,
            nameWithExtension = nameWithExtension
        )
    val flipperFilePath: FlipperFilePath
        get() = FlipperFilePath(
            folder = extFolderPath,
            nameWithExtension = nameWithExtension
        )

    val irFileIdOrNull: Long?
        get() = when (this) {
            is Id -> irFileId
            is Path -> null
        }
}
