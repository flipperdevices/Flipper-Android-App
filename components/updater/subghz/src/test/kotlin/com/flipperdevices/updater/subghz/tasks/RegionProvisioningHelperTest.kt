package com.flipperdevices.updater.subghz.tasks

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.telephony.TelephonyManager
import com.flipperdevices.updater.subghz.helpers.RegionProvisioningHelperImpl
import com.flipperdevices.updater.subghz.model.RegionProvisioningSource
import io.mockk.mockk
import java.util.Locale
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
import org.robolectric.util.ReflectionHelpers

class RegionProvisioningHelperTest {
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val context: Context = mockk()
    private val telephonyManager: TelephonyManager = mockk()
    private val underTest = RegionProvisioningHelperImpl(context)

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        reset(context)
        reset(telephonyManager)
        whenever(
            context.getSystemService(eq(TelephonyManager::class.java))
        ).doReturn(telephonyManager)
        mockLocale(context, Locale.ROOT)
    }

    @Test
    fun `provide region from sim network`() = runTest {
        whenever(telephonyManager.networkCountryIso).doReturn("WW")

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
        whenever(telephonyManager.simCountryIso).doReturn("WW")

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
        whenever(telephonyManager.networkCountryIso).doReturn("WW")
        whenever(telephonyManager.isNetworkRoaming).doReturn(true)

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
        whenever(context.resources).doReturn(mock())
        val configuration: Configuration = mock()
        whenever(context.resources.configuration).doReturn(configuration)
        configuration.locale = Locale.CANADA
        whenever(configuration.locales).doReturn(LocaleList(Locale.CHINA))

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
        whenever(context.resources).doReturn(mock())
        val configuration: Configuration = mock()
        whenever(context.resources.configuration).doReturn(configuration)
        configuration.locale = Locale.CANADA
        val locales: LocaleList = mock()
        whenever(locales.get(eq(0))).doReturn(Locale.CHINA)
        whenever(configuration.locales).doReturn(locales)

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
    whenever(context.resources).doReturn(mock())
    val configuration: Configuration = mock()
    whenever(context.resources.configuration).doReturn(configuration)
    configuration.locale = locale
    whenever(configuration.locales).doReturn(LocaleList(locale))
}
