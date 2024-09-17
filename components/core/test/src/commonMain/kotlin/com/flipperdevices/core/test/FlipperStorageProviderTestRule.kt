package com.flipperdevices.core.test

import com.flipperdevices.core.FlipperStorageProvider
import okio.fakefilesystem.FakeFileSystem
import org.junit.rules.ExternalResource

class FlipperStorageProviderTestRule : ExternalResource() {
    lateinit var flipperStorageProvider: FlipperStorageProvider

    override fun before() {
        super.before()
        flipperStorageProvider = TestFlipperStorageProvider()
    }

    override fun after() {
        super.after()
        (flipperStorageProvider.fileSystem as FakeFileSystem).checkNoOpenFiles()
    }
}
