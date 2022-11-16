package com.flipperdevices.share.cryptostorage

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.share.cryptostorage.helper.CryptoHelperApiImpl
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptoHelperTest {

    private val cryptoHelper = CryptoHelperApiImpl()

    @Test
    fun `Decode and encode array`() {
        val data = ByteArray(128) { (-32 until 32).random().toByte() }
        val encodeData = cryptoHelper.encrypt(data)
        val decodeData = cryptoHelper.decrypt(encodeData.data, encodeData.key)
        Assert.assertArrayEquals(data, decodeData)
    }
}
