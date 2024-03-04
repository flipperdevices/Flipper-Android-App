package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InstalledFapsUidsProducerTest {
    private val fapManifestStateFlow = MutableStateFlow<FapManifestState>(
        FapManifestState.Loaded(
            items = persistentListOf(),
            inProgress = true
        )
    )
    private lateinit var underTest: InstalledFapsUidsProducer

    @Before
    fun setUp() {
        val fapManifestApi = mockk<FapManifestApi>(relaxUnitFun = true) {
            every { getManifestFlow() } returns fapManifestStateFlow
        }
        underTest = InstalledFapsUidsProducer(
            queueApi = mockk(relaxUnitFun = true),
            fapManifestApi = fapManifestApi
        )
    }

    @Test
    fun `loading in progress with one item`() = runTest {
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST")),
                inProgress = true
            )
        )

        underTest.refresh(this, force = true)

        advanceUntilIdle()

        Assert.assertEquals(
            underTest.getUidsStateFlow().value,
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        OfflineFapApp(getTestManifest("TEST"))
                    )
                ),
                inProgress = true
            )
        )
    }
}

private fun getTestManifest(uid: String) = FapManifestItem(
    applicationAlias = "",
    uid = "",
    versionUid = "",
    path = "",
    fullName = "",
    iconBase64 = null,
    sdkApi = null,
    sourceFileHash = null
)