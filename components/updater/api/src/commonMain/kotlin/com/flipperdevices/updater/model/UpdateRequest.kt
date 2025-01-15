package com.flipperdevices.updater.model

import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UpdateRequest(
    val updateFrom: FirmwareVersion,
    val updateTo: FirmwareVersion,
    val changelog: String?,
    val content: UpdateContent,
    val requestId: Long = System.currentTimeMillis()
)

@Serializable
sealed class UpdateContent {
    abstract fun folderName(): String
}

@Serializable
class OfficialFirmware(
    val distributionFile: DistributionFile
) : UpdateContent() {
    override fun folderName(): String = distributionFile.sha256 ?: distributionFile.url
}

@Serializable
class InternalStorageFirmware(private val uriString: String) : UpdateContent() {

    @Transient
    val uri: Uri = Uri.parse(uriString)

    override fun folderName(): String {
        return uri
            .path
            ?.substringBeforeLast("/")
            ?.substringAfterLast(".") ?: uri.toString()
    }
}

@Serializable
class WebUpdaterFirmware(val url: String) : UpdateContent() {
    override fun folderName(): String = url.substringBeforeLast("/")
}
