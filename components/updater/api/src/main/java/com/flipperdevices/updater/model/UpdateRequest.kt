package com.flipperdevices.updater.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Parcelize
@Serializable
data class UpdateRequest(
    val updateFrom: FirmwareVersion,
    val updateTo: FirmwareVersion,
    val changelog: String?,
    val content: UpdateContent,
    val requestId: Long = System.currentTimeMillis()
) : Parcelable {
    fun encode(): String = Json.encodeToString(serializer(), this)
}

@Parcelize
@Serializable
sealed class UpdateContent : Parcelable {
    abstract fun folderName(): String
}

@Parcelize
@Serializable
class OfficialFirmware(
    val distributionFile: DistributionFile
) : UpdateContent(), Parcelable {
    override fun folderName(): String = distributionFile.sha256 ?: distributionFile.url
}

@Parcelize
@Serializable
class InternalStorageFirmware(private val uriString: String) : UpdateContent(), Parcelable {

    @IgnoredOnParcel
    @Transient
    val uri: Uri = Uri.parse(uriString)

    override fun folderName(): String {
        return uri
            .path
            ?.substringBeforeLast("/")
            ?.substringAfterLast(".") ?: uri.toString()
    }
}

@Parcelize
@Serializable
class WebUpdaterFirmware(val url: String) : UpdateContent(), Parcelable {
    override fun folderName(): String = url.substringBeforeLast("/")
}
