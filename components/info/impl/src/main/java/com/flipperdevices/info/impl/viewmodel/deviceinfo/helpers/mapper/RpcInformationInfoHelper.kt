package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.mapper

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.info.api.model.RadioStackType

object RpcInformationInfoHelper {
    // softwareRevision Branch.Commit
    fun softwareRevision(firmwareCommit: String?, firmwareBranch: String?): String? {
        return if (isNotNull(firmwareCommit, firmwareBranch)) {
            val firmwareBranchCapitalize = firmwareBranch?.titlecaseFirstCharIfItIsLowercase()
            "$firmwareBranchCapitalize $firmwareCommit"
        } else {
            null
        }
    }

    // protobuf Major.Minor
    fun protobufVersion(protobufMajor: String?, protobufMinor: String?): SemVer? {
        return if (isNotNull(protobufMajor, protobufMinor)) {
            SemVer(
                protobufMajor?.toIntOrNull() ?: 0,
                protobufMinor?.toIntOrNull() ?: 0
            )
        } else {
            null
        }
    }

    // deviceInfo Major.Minor
    fun deviceInfoVersion(deviceInfoMajor: String?, deviceInfoMinor: String?): SemVer? {
        return if (isNotNull(deviceInfoMajor, deviceInfoMinor)) {
            SemVer(
                deviceInfoMajor?.toIntOrNull() ?: 0,
                deviceInfoMinor?.toIntOrNull() ?: 0
            )
        } else {
            null
        }
    }

    // radio Major.Minor.Type
    fun radioFirmware(
        radioMajor: String?,
        radioMinor: String?,
        radioType: String?
    ): String? {
        return if (isNotNull(radioMajor, radioMinor, radioType)) {
            "$radioMajor.$radioMinor.$radioType"
        } else {
            null
        }
    }

    fun radioType(radioType: String?): RadioStackType? {
        return RadioStackType.find(radioType)
    }
}
