package com.flipperdevices.info.impl.fragment

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ktx.jre.isNotNull
import com.flipperdevices.core.ktx.jre.titlecaseFirstCharIfItIsLowercase
import com.flipperdevices.info.impl.model.DeviceFullInfo
import com.flipperdevices.info.impl.model.FirmwareInfo
import com.flipperdevices.info.impl.model.FlipperDeviceInfo
import com.flipperdevices.info.impl.model.OtherInfo
import com.flipperdevices.info.impl.model.RadioStackInfo

object DeviceInfoHelper {
    @Composable
    fun parseFields(fields: Map<String, String>): DeviceFullInfo {
        val flipperDeviceInfo = FlipperDeviceInfo(
            deviceName = fields["hardware_name"],
            hardwareModel = fields["hardware_model"],
            hardwareRegion = fields["hardware_region"],
            hardwareRegionProv = fields["hardware_region_provisioned"],
            hardwareVersion = fields["hardware_ver"],
            hardwareOTPVersion = fields["hardware_otp_ver"],
            serialNumber = fields["hardware_uid"]
        )

        val firmwareCommit = fields["firmware_commit"]
        val firmwareBranch = fields["firmware_branch"]
        val softwareRevision = softwareRevision(firmwareCommit, firmwareBranch)

        val protobufMajor = fields["protobuf_version_major"]
        val protobufMinor = fields["protobuf_version_minor"]
        val protobufVersion = protobufVersion(protobufMajor, protobufMinor)

        val firmwareInfo = FirmwareInfo(
            firmwareCommit = firmwareCommit,
            softwareRevision = softwareRevision,
            buildDate = fields["firmware_build_date"],
            target = fields["firmware_target"],
            protobufVersion = protobufVersion
        )

        val radioMajor = fields["radio_stack_major"]
        val radioMinor = fields["radio_stack_minor"]
        val radioType = fields["radio_stack_type"]

        val radioStackInfo = RadioStackInfo(
            radioFirmware = radioFirmware(radioMajor, radioMinor, radioType)
        )

        val otherInfo = OtherInfo(
            fields = fields.filterNot { usedFields.contains(it.key) }.entries
        )

        return DeviceFullInfo(
            flipperDeviceInfo,
            firmwareInfo,
            radioStackInfo,
            otherInfo
        )
    }

    // branch + commit
    private fun softwareRevision(firmwareCommit: String?, firmwareBranch: String?): String? {
        return if (isNotNull(firmwareCommit, firmwareBranch)) {
            val firmwareBranchCapitalize = firmwareBranch?.titlecaseFirstCharIfItIsLowercase()
            "$firmwareBranchCapitalize $firmwareCommit"
        } else null
    }

    // protobuf Major.Minor
    private fun protobufVersion(protobufMajor: String?, protobufMinor: String?): String? {
        return if (isNotNull(protobufMajor, protobufMinor)) "$protobufMajor.$protobufMinor"
        else null
    }

    // radio Major.Minor.Type (NameType)
    private fun radioFirmware(
        radioMajor: String?,
        radioMinor: String?,
        radioType: String?
    ): String? {
        val radioTypeName: String? = when (radioType) {
            "1" -> "Full"
            "3" -> "Light"
            "4" -> "Beacon"
            "5" -> "Basic"
            "6" -> "Full Ext Adv"
            "7" -> "HCI Ext Adv"
            else -> null
        }
        return if (isNotNull(radioMajor, radioMinor, radioType, radioTypeName)) {
            "$radioMajor.$radioMinor.$radioType ($radioTypeName)"
        } else null
    }

    // This fields use in NOT other section
    private val usedFields = listOf(
        "hardware_name",
        "hardware_model",
        "hardware_region",
        "hardware_region_provisioned",
        "hardware_ver",
        "hardware_otp_ver",
        "hardware_uid",
        "firmware_commit",
        "firmware_branch",
        "firmware_build_date",
        "firmware_target",
        "protobuf_version_major",
        "protobuf_version_minor",
        "radio_stack_major",
        "radio_stack_minor",
        "radio_stack_type"
    )
}
