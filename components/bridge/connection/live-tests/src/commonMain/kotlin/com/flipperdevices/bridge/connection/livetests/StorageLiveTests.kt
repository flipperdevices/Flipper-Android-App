package com.flipperdevices.bridge.connection.livetests

import com.flipperdevices.bridge.connection.livetests.api.LiveTest
import com.flipperdevices.bridge.connection.livetests.tests.FileTransferLiveTest
import com.flipperdevices.bridge.connection.livetests.tests.LargeFileTransferLiveTest
import dev.zacsweers.metro.Inject

class StorageLiveTests @Inject constructor(
    fileTransferLiveTest: FileTransferLiveTest,
    largeFileTransferLiveTest: LargeFileTransferLiveTest
) {
    val orderedTests: List<LiveTest> = listOf(
        fileTransferLiveTest,
        largeFileTransferLiveTest
    )
}
