package com.flipperdevices.bridge.connection.livetests

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.tests.ReconnectLiveTest
import com.flipperdevices.bridge.connection.livetests.tests.SoundLiveTest
import dev.zacsweers.metro.Inject

/**
 * Defines which live tests run and in which order.
 *
 * The reconnect test goes last because it churns the connection
 * the other tests rely on.
 */
class LiveTestsSuite @Inject constructor(
    rpcLiveTests: RpcLiveTests,
    storageLiveTests: StorageLiveTests,
    soundLiveTest: SoundLiveTest,
    reconnectLiveTest: ReconnectLiveTest
) {
    val orderedTests: List<LiveTest> =
        rpcLiveTests.orderedTests +
            storageLiveTests.orderedTests +
            listOf(soundLiveTest, reconnectLiveTest)
}
