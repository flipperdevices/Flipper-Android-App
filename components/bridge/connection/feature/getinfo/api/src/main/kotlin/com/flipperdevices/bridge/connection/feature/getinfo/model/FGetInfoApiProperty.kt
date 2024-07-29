package com.flipperdevices.bridge.connection.feature.getinfo.model

sealed interface FGetInfoApiProperty {
    val group: FGetInfoApiGroup
    val key: String

    enum class DeviceInfo(override val key: String) : FGetInfoApiProperty {
        DEVICE_NAME("hardware.name"),
        HARDWARE_MODEL("hardware.model"),
        HARDWARE_REGION("hardware.region.builtin"),
        HARDWARE_REGION_PROV("hardware.region.provisioned"),
        HARDWARE_VERSION("hardware.ver"),
        HARDWARE_OTP_VERSION("hardware.otp.ver"),
        SERIAL_NUMBER("hardware.uid"),
        FIRMWARE_COMMIT("firmware.commit.hash"),
        FIRMWARE_BRANCH("firmware.branch.name"),
        FIRMWARE_BUILD_DATE("firmware.build.date"),
        FIRMWARE_TARGET("firmware.target"),
        PROTOBUF_MAJOR("protobuf.version.major"),
        PROTOBUF_MINOR("protobuf.version.minor"),
        DEVICE_INFO_MAJOR("format.major"),
        DEVICE_INFO_MINOR("format.minor"),
        RADIO_STACK_MAJOR("radio.stack.major"),
        RADIO_STACK_MINOR("radio.stack.minor"),
        RADIO_STACK_TYPE("radio.stack.type"),
        RADIO_STACK_SUB("radio.stack.sub"),
        FIRMWARE_FORK("firmware.origin.fork"),
        FIRMWARE_ORIGIN("firmware.origin.git");

        override val group = FGetInfoApiGroup.DEVICE_INFO
    }

    data class Unknown(
        override val group: FGetInfoApiGroup,
        override val key: String
    ) : FGetInfoApiProperty

    val path
        get() = "${group.key}.$key"
}
