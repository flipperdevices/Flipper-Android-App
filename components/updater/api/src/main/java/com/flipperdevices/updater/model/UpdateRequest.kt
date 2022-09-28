package com.flipperdevices.updater.model

import android.os.Parcelable
import com.flipperdevices.core.ktx.jre.getClearName
import java.io.File
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

class OfficialFirmware(
    val distributionFile: DistributionFile
) : UpdateContent() {
    override fun folderName(): String = distributionFile.sha256 ?: distributionFile.url
}

class InternalStorageFirmware(val file: File) : UpdateContent() {
    override fun folderName(): String {
        return file.getClearName()
    }
}

class WebUpdaterFirmware(val url: String) : UpdateContent() {
    override fun folderName(): String = url
}
