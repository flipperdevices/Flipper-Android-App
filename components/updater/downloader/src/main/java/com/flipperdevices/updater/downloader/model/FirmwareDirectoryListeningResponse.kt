package com.flipperdevices.updater.downloader.model

import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FirmwareDirectoryListeningResponse(
    @SerialName("channels")
    val channels: List<FirmwareVersionChannel>
)

@Serializable
internal enum class FirmwareChannelEnum(val original: FirmwareChannel) {
    @SerialName("development")
    DEV(FirmwareChannel.DEV),

    @SerialName("release")
    RELEASE(FirmwareChannel.RELEASE),

    @SerialName("release-candidate")
    RELEASE_CANDIDATE(FirmwareChannel.RELEASE_CANDIDATE)
}

@Serializable
internal data class FirmwareVersionChannel(
    @SerialName("id")
    val id: FirmwareChannelEnum? = null,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("versions")
    val versions: List<FirmwareVersion>? = null
)

@Serializable
internal data class FirmwareVersion(
    @SerialName("version")
    val version: String,
    @SerialName("changelog")
    val changelog: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("files")
    val files: List<FirmwareFile>
)

@Serializable
internal data class FirmwareFile(
    @SerialName("url")
    val url: String,
    @SerialName("target")
    val target: Target? = null,
    @SerialName("type")
    val type: ArtifactType? = null,
    @SerialName("sha256")
    val sha256: String
)

@Serializable
internal enum class Target {
    @SerialName("any")
    ANY,

    @SerialName("f7")
    F7
}

@Serializable
internal enum class ArtifactType {
    @SerialName("update_tgz")
    UPDATE_TGZ
}
