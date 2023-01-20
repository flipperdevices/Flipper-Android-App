package com.flipperdevices.updater.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateRequest(
    val updateFrom: FirmwareVersion,
    val updateTo: FirmwareVersion,
    val changelog: String?,
    val content: UpdateContent,
    val requestId: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
sealed class UpdateContent : Parcelable {
    abstract fun folderName(): String
}

@Parcelize
class OfficialFirmware(
    val distributionFile: DistributionFile
) : UpdateContent(), Parcelable {
    override fun folderName(): String = distributionFile.sha256 ?: distributionFile.url
}

@Parcelize
class InternalStorageFirmware(val uri: Uri) : UpdateContent(), Parcelable {
    override fun folderName(): String {
        return uri
            .path
            ?.substringBeforeLast("/")
            ?.substringAfterLast(".") ?: uri.toString()
    }
}

@Parcelize
class WebUpdaterFirmware(val url: String) : UpdateContent(), Parcelable {
    override fun folderName(): String = url.substringBeforeLast("/")
}
