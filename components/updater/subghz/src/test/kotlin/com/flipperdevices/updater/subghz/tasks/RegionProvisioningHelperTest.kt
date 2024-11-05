package com.flipperdevices.updater.subghz.tasks

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.telephony.TelephonyManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.core.buildkonfig.BuildKonfig
import com.flipperdevices.updater.subghz.helpers.RegionProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.RegionProvisioningHelperImpl
import com.flipperdevices.updater.subghz.model.RegionProvisioningSource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildKonfig.ROBOELECTRIC_SDK_VERSION])
class RegionProvisioningHelperTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var context: Context
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var underTest: RegionProvisioningHelper

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        telephonyManager = mockk {
            every { simCountryIso } returns null
            every { networkCountryIso } returns null
            every { isNetworkRoaming } returns false
        }
        context = mockk {
            every {
                getSystemService(eq(TelephonyManager::class.java))
            } returns telephonyManager
        }
        underTest = RegionProvisioningHelperImpl(context)

        mockLocale(context, Locale.ROOT)
    }

    @Test
    fun `provide region from sim network`() = runTest {
        every { telephonyManager.networkCountryIso } returns "WW"

        val actualRegion = underTest.provideRegion(null)

        Assert.assertEquals("WW", actualRegion.regionFromNetwork)
        Assert.assertEquals(
            "WW" to RegionProvisioningSource.SIM_NETWORK,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromSim)
        Assert.assertNull(actualRegion.regionFromIp)
        Assert.assertTrue(actualRegion.regionSystem?.isEmpty() ?: false)
        Assert.assertFalse(actualRegion.isRoaming)
    }

    @Test
    fun `provide region from sim country`() = runTest {
        every { telephonyManager.simCountryIso } returns "WW"

        val actualRegion = underTest.provideRegion(null)

        Assert.assertEquals("WW", actualRegion.regionFromSim)
        Assert.assertEquals(
            "WW" to RegionProvisioningSource.SIM_COUNTRY,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromNetwork)
        Assert.assertNull(actualRegion.regionFromIp)
        Assert.assertTrue(actualRegion.regionSystem?.isEmpty() ?: false)
        Assert.assertFalse(actualRegion.isRoaming)
    }

    @Test
    fun `provide region from sim network when roaming`() = runTest {
        every { telephonyManager.networkCountryIso } returns "WW"
        every { telephonyManager.isNetworkRoaming } returns true

        val actualRegion = underTest.provideRegion(null)

        Assert.assertEquals("WW", actualRegion.regionFromNetwork)
        Assert.assertEquals(
            "WW" to RegionProvisioningSource.SIM_NETWORK,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromSim)
        Assert.assertNull(actualRegion.regionFromIp)
        Assert.assertTrue(actualRegion.regionSystem?.isEmpty() ?: false)
        Assert.assertTrue(actualRegion.isRoaming)
    }

    @Test
    fun `provide region from system`() = runTest {
        ReflectionHelpers.setStaticField(
            Build.VERSION::class.java,
            "SDK_INT",
            Build.VERSION_CODES.M
        )
        every { context.resources } returns mockk()
        val configuration: Configuration = mockk()
        every { context.resources.configuration } returns configuration
        configuration.locale = Locale.CANADA
        every { configuration.locales } returns LocaleList(Locale.CHINA)

        val actualRegion = underTest.provideRegion(null)

        Assert.assertEquals("CA", actualRegion.regionSystem)
        Assert.assertEquals(
            "CA" to RegionProvisioningSource.SYSTEM,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromNetwork)
        Assert.assertNull(actualRegion.regionFromSim)
        Assert.assertNull(actualRegion.regionFromIp)
        Assert.assertFalse(actualRegion.isRoaming)
    }

    @Test
    fun `provide region from system after android n`() = runTest {
        ReflectionHelpers.setStaticField(
            Build.VERSION::class.java,
            "SDK_INT",
            Build.VERSION_CODES.N
        )

        every { context.resources } returns mockk()
        val configuration: Configuration = mockk()
        every { context.resources.configuration } returns configuration
        configuration.locale = Locale.CANADA
        val locales: LocaleList = mockk()
        every { locales.get(eq(0)) } returns Locale.CHINA
        every { configuration.locales } returns locales

        val actualRegion = underTest.provideRegion(null)

        Assert.assertEquals("CN", actualRegion.regionSystem)
        Assert.assertEquals(
            "CN" to RegionProvisioningSource.SYSTEM,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromNetwork)
        Assert.assertNull(actualRegion.regionFromSim)
        Assert.assertNull(actualRegion.regionFromIp)
        Assert.assertFalse(actualRegion.isRoaming)
    }

    @Test
    fun `provide region from default`() = runTest {
        val actualRegion = underTest.provideRegion("WW")

        Assert.assertEquals("WW", actualRegion.regionFromIp)
        Assert.assertEquals(
            "WW" to RegionProvisioningSource.GEO_IP,
            actualRegion.provideRegion()
        )
        Assert.assertNull(actualRegion.regionFromSim)
        Assert.assertNull(actualRegion.regionFromNetwork)
        Assert.assertTrue(actualRegion.regionSystem?.isEmpty() ?: false)
        Assert.assertFalse(actualRegion.isRoaming)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}

private fun mockLocale(context: Context, locale: Locale) {
    every { context.resources } returns mockk()
    val configuration: Configuration = mockk()
    every { context.resources.configuration } returns configuration
    configuration.locale = locale
    every { configuration.locales } returns LocaleList(locale)
}
