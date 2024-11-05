package com.flipperdevices.keyparser.impl.url

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.keyparser.impl.parsers.url.FFFUrlDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class FFFUrlDecoderTest {
    private val underTest = FFFUrlDecoder()

    @Test
    fun `on wrong scheme null`() {
        val testUri = Uri.fromParts("content", "flpr.app/s", null)

        val actualResult = underTest.uriToContent(testUri)

        assertNull(actualResult)
    }

    @Test
    fun `on wrong host null`() {
        val testUri = Uri.parse("https://google.com/s")

        val actualResult = underTest.uriToContent(testUri)

        assertNull(actualResult)
    }

    @Test
    fun `on wrong path null`() {
        val testUri = Uri.parse("https://flpr.app/wrongpath")

        val actualResult = underTest.uriToContent(testUri)

        assertNull(actualResult)
    }

    @Test
    fun `on empty content crash`() {
        val testUri = Uri.parse("https://flpr.app/s")

        val exception = runCatching {
            underTest.uriToContent(testUri)
        }.exceptionOrNull()

        assertNotNull(exception)
        assertTrue(
            "Exception type should be IllegalArgumentException",
            exception is IllegalArgumentException
        )
        assertEquals("Sharing file content can't be empty", exception?.message)
    }

    @Test
    fun `correct decoding value`() {
        val testUri = Uri.parse("https://flpr.app/s#path=nfc%2FUid+card+name.nfc")

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, _) = actualResult!!

        assertEquals(path, "nfc/Uid card name.nfc")
    }

    @Test
    fun `fff without path throws exception`() {
        val testUri = Uri.parse("https://flpr.app/s#foo=bar")

        val exception = runCatching {
            underTest.uriToContent(testUri)
        }.exceptionOrNull()

        assertNotNull(exception)
        assertTrue(
            "Exception type should be IllegalArgumentException",
            exception is IllegalArgumentException
        )
        assertEquals("Url fragment doesn't contains path", exception?.message)
    }

    @Test
    fun `path not included in fff content`() {
        val testUri = Uri.parse("https://flpr.app/s#path=test&foo=bar")

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, content) = actualResult!!

        assertEquals("test", path)
        assertEquals(1, content.orderedDict.size)
        assertEquals("foo" to "bar", content.orderedDict.first())
    }

    @Test
    fun `only first path exclude`() {
        val testUri = Uri.parse("https://flpr.app/s#path=test&foo=bar&path=otherTest")

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, content) = actualResult!!

        assertEquals("test", path)
        assertEquals(2, content.orderedDict.size)
        assertEquals("foo" to "bar", content.orderedDict[0])
        assertEquals("path" to "otherTest", content.orderedDict[1])
    }

    @Test
    fun `path can be not first`() {
        val testUri = Uri.parse("https://flpr.app/s#foo=bar&path=test")

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, content) = actualResult!!

        assertEquals("test", path)
        assertEquals(1, content.orderedDict.size)
    }

    @Test
    fun `path key ignore case`() {
        val testUri = Uri.parse("https://flpr.app/s#foo=bar&pAtH=test")

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, content) = actualResult!!

        assertEquals("test", path)
        assertEquals(1, content.orderedDict.size)
    }

    @Test
    fun `parse successful correct url`() {
        val testUri = Uri.parse(
            "https://flpr.app/s#path=nfc%2FUid_card_name.nfc&" +
                "Filetype=Flipper+NFC+device&" +
                "Version=2&" +
                "Device+type=UID&UID=F2+85+E2+3B&" +
                "ATQA=04+00&SAK=08"
        )

        val actualResult = underTest.uriToContent(testUri)
        assertNotNull(actualResult)
        val (path, content) = actualResult!!

        assertEquals("nfc/Uid_card_name.nfc", path)
        assertEquals("Filetype" to "Flipper NFC device", content.orderedDict[0])
        assertEquals("Version" to "2", content.orderedDict[1])
        assertEquals("Device type" to "UID", content.orderedDict[2])
        assertEquals("UID" to "F2 85 E2 3B", content.orderedDict[3])
        assertEquals("ATQA" to "04 00", content.orderedDict[4])
        assertEquals("SAK" to "08", content.orderedDict[5])
    }
}
