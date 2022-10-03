package com.flipperdevices.keyedit.api

import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotSavedFlipperKey(
    val mainFile: NotSavedFlipperFile,
    val additionalFiles: List<NotSavedFlipperFile>,
    val notes: String?
) : Parcelable

@Parcelize
data class NotSavedFlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent.InternalFile
) : Parcelable
