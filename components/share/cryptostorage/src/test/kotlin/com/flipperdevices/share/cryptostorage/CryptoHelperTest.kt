package com.flipperdevices.share.cryptostorage

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.share.cryptostorage.helper.CryptoHelperApiImpl
import org.junit.Assert
import org.junit.Test

class CryptoHelperTest {

    private val cryptoHelper = CryptoHelperApiImpl()
    private val flipperKey = FlipperKey(
        mainFile = FlipperFile(
            path = FlipperFilePath(
                folder = "test",
                nameWithExtension = "test"
            ),
            content = FlipperKeyContent.RawData("test".toByteArray())
        ),
        synchronized = false,
        deleted = false
    )

    @Test
    fun `Decode and encode array`() {
        val encodeData = cryptoHelper.encrypt(flipperKey.mainFile)
        val decodeData = cryptoHelper.decrypt(encodeData.data, encodeData.key)
        Assert.assertEquals(flipperKey.keyContent, decodeData)
    }
}
