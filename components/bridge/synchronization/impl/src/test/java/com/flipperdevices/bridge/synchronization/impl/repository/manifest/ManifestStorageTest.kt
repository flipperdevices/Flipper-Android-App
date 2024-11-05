package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.core.test.readTestAsset
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class ManifestStorageTest {
    @get:Rule
    var folder = TemporaryFolder()

    private lateinit var underTest: ManifestStorage
    private lateinit var testFolder: File

    @Before
    fun setUp() {
        testFolder = folder.newFolder("manifest")
        val context = mockk<Context> {
            every { filesDir } returns testFolder
        }
        underTest = ManifestStorageImpl(context)
    }

    @Test
    fun `empty manifest read`() = runTest {
        val actual = underTest.load()
        assertNull(actual)
    }

    @Test
    fun `empty manifest write`() = runTest {
        underTest.update { it }
        val actual = underTest.load()
        assertNotNull(actual)
    }

    @Test
    fun `keys manifest write`() = runTest {
        underTest.update {
            it.copy(keys = listOf(KeyWithHash(FlipperFilePath.DUMMY, "HASH")))
        }

        val actual = underTest.load()

        assertNotNull(actual)
        val expected = listOf(KeyWithHash(FlipperFilePath.DUMMY, "HASH"))
        Assert.assertEquals(expected, actual!!.keys)
        Assert.assertTrue(actual.favorites.isEmpty())
        Assert.assertTrue(actual.favoritesFromFlipper.isEmpty())
    }

    @Test
    fun `favorites manifest write`() = runTest {
        underTest.update {
            it.copy(favorites = listOf(FlipperFilePath.DUMMY))
        }

        val actual = underTest.load()

        assertNotNull(actual)
        val expected = listOf(FlipperFilePath.DUMMY)
        Assert.assertEquals(expected, actual!!.favorites)
        Assert.assertTrue(actual.keys.isEmpty())
        Assert.assertTrue(actual.favoritesFromFlipper.isEmpty())
    }

    @Test
    fun `favorites on flipper manifest write`() = runTest {
        underTest.update {
            it.copy(favoritesFromFlipper = listOf(FlipperFilePath.DUMMY))
        }

        val actual = underTest.load()

        assertNotNull(actual)
        val expected = listOf(FlipperFilePath.DUMMY)
        Assert.assertEquals(expected, actual!!.favoritesFromFlipper)
        Assert.assertTrue(actual.keys.isEmpty())
        Assert.assertTrue(actual.favorites.isEmpty())
    }

    @Test
    fun `migrate before from favorites on flipper`() = runTest {
        val manifestFile = File(testFolder, "LastSyncManifest_v4.json")
        manifestFile.writeBytes(readTestAsset("LastSyncManifest_v4.json"))

        val actual = underTest.load()
        Assert.assertNotNull(actual)
        Assert.assertEquals(
            listOf(
                KeyWithHash(
                    FlipperFilePath("ibutton", "Cyfral.ibtn"),
                    "bc49362f001c74332b4ac8a4efa8a897"
                ),
                KeyWithHash(
                    FlipperFilePath("ibutton", "Dallas.ibtn"),
                    "3db7a8948e546f4d5a44027b3f17e0c0"
                ),
                KeyWithHash(
                    FlipperFilePath("infrared", "Ac_un.ir"),
                    "f7a77047bbf8895ae2db6ce1b031cd34"
                )
            ),
            actual!!.keys
        )
        Assert.assertEquals(
            listOf(
                FlipperFilePath("lfrfid", "Pines.rfid"),
                FlipperFilePath("nfc", "Gudauri.nfc")
            ),
            actual.favorites
        )
        Assert.assertTrue(actual.favoritesFromFlipper.isEmpty())
    }
}
