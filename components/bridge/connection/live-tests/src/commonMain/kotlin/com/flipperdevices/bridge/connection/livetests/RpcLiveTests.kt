package com.flipperdevices.bridge.connection.livetests

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.tests.DeviceInfoLiveTest
import com.flipperdevices.bridge.connection.livetests.tests.PingLiveTest
import com.flipperdevices.bridge.connection.livetests.tests.PingStressLiveTest
import dev.zacsweers.metro.Inject

class RpcLiveTests @Inject constructor(
    pingLiveTest: PingLiveTest,
    deviceInfoLiveTest: DeviceInfoLiveTest,
    pingStressLiveTest: PingStressLiveTest
) {
    val orderedTests: List<LiveTest> = listOf(
        pingLiveTest,
        deviceInfoLiveTest,
        pingStressLiveTest
    )
}
