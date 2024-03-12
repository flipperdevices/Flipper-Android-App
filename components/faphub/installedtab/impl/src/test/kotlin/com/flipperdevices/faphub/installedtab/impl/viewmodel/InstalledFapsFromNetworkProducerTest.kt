package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp
import com.flipperdevices.faphub.installedtab.impl.model.InstalledNetworkErrorEnum
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("LongMethod")
class InstalledFapsFromNetworkProducerTest {
    private lateinit var fapStateManager: FapInstallationStateManager
    private lateinit var fapNetworkApi: FapNetworkApi
    private lateinit var flipperTargetFlow: MutableStateFlow<FlipperTarget?>
    private lateinit var uidsStateFlow: MutableStateFlow<FapInstalledUidsState>
    private lateinit var underTest: InstalledFapsFromNetworkProducer
    private lateinit var installedFapsUidsProducer: InstalledFapsUidsProducer
    private lateinit var flipperTargetProviderApi: FlipperTargetProviderApi

    @Before
    fun setUp() {
        uidsStateFlow = MutableStateFlow(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(),
                inProgress = true
            )
        )
        flipperTargetFlow = MutableStateFlow(null)
        fapNetworkApi = mockk()
        fapStateManager = mockk {
            every { getFapStateFlow(any(), any()) } returns flowOf(FapState.Installed)
        }
        installedFapsUidsProducer = mockk(relaxUnitFun = true) {
            every { getUidsStateFlow() } returns uidsStateFlow
        }
        flipperTargetProviderApi = mockk {
            every { getFlipperTarget() } returns flipperTargetFlow
        }
    }

    @Test
    fun `load one item`() = runTest {
        val childScope = TestScope(this.testScheduler)
        underTest = InstalledFapsFromNetworkProducer(
            installedFapsUidsProducer = installedFapsUidsProducer,
            flipperTargetProviderApi = flipperTargetProviderApi,
            fapNetworkApi = fapNetworkApi,
            fapStateManager = fapStateManager,
            globalScope = childScope
        )
        uidsStateFlow.emit(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST")
                        )
                    )
                ),
                inProgress = true
            )
        )
        val testTarget = FlipperTarget.Received(target = "", sdk = SemVer(1, 0))
        flipperTargetFlow.emit(FlipperTarget.Received(target = "", sdk = SemVer(1, 0)))
        coEvery {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        } returns Result.success(listOf(getTestFapItemShort("TEST", name = "From Internet")))

        underTest.refresh(force = true)

        val state = underTest.getLoadedFapsFlow().filter {
            it is FapInstalledInternalLoadingState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledInternalLoadingState.Loaded(
                faps = persistentListOf(
                    InstalledFapApp.OnlineFapApp(
                        getTestFapItemShort("TEST", name = "From Internet")
                    ) to FapInstalledInternalState.Installed
                ),
                inProgress = true
            ),
            state
        )

        childScope.cancel()
    }

    @Test
    fun `load one item offline`() = runTest {
        val childScope = TestScope(this.testScheduler)
        underTest = InstalledFapsFromNetworkProducer(
            installedFapsUidsProducer = installedFapsUidsProducer,
            flipperTargetProviderApi = flipperTargetProviderApi,
            fapNetworkApi = fapNetworkApi,
            fapStateManager = fapStateManager,
            globalScope = childScope
        )
        uidsStateFlow.emit(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST")
                        )
                    )
                ),
                inProgress = true
            )
        )
        val testTarget = FlipperTarget.Received(target = "", sdk = SemVer(1, 0))
        flipperTargetFlow.emit(FlipperTarget.Received(target = "", sdk = SemVer(1, 0)))
        val testException = RuntimeException("error fetching from internet")
        coEvery {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        } returns Result.failure(testException)

        underTest.refresh(force = true)

        val state = underTest.getLoadedFapsFlow().filter {
            it is FapInstalledInternalLoadingState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledInternalLoadingState.Loaded(
                faps = persistentListOf(
                    InstalledFapApp.OfflineFapApp(
                        getTestManifest("TEST")
                    ) to FapInstalledInternalState.InstalledOffline
                ),
                inProgress = true,
                networkError = InstalledNetworkErrorEnum.GENERAL
            ),
            state
        )

        childScope.cancel()
    }

    @Test
    fun `load one item not found`() = runTest {
        val childScope = TestScope(this.testScheduler)
        underTest = InstalledFapsFromNetworkProducer(
            installedFapsUidsProducer = installedFapsUidsProducer,
            flipperTargetProviderApi = flipperTargetProviderApi,
            fapNetworkApi = fapNetworkApi,
            fapStateManager = fapStateManager,
            globalScope = childScope
        )
        uidsStateFlow.emit(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST")
                        )
                    )
                ),
                inProgress = true
            )
        )
        val testTarget = FlipperTarget.Received(target = "", sdk = SemVer(1, 0))
        flipperTargetFlow.emit(FlipperTarget.Received(target = "", sdk = SemVer(1, 0)))
        coEvery {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        } returns Result.success(emptyList())

        underTest.refresh(force = true)

        val state = underTest.getLoadedFapsFlow().filter {
            it is FapInstalledInternalLoadingState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledInternalLoadingState.Loaded(
                faps = persistentListOf(
                    InstalledFapApp.OfflineFapApp(
                        getTestManifest("TEST")
                    ) to FapInstalledInternalState.InstalledOffline
                ),
                inProgress = true
            ),
            state
        )

        childScope.cancel()
    }

    @Test
    fun `request only new ids`() = runTest {
        val childScope = TestScope(this.testScheduler)
        underTest = InstalledFapsFromNetworkProducer(
            installedFapsUidsProducer = installedFapsUidsProducer,
            flipperTargetProviderApi = flipperTargetProviderApi,
            fapNetworkApi = fapNetworkApi,
            fapStateManager = fapStateManager,
            globalScope = childScope
        )
        uidsStateFlow.emit(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST1")
                        )
                    )
                ),
                inProgress = true
            )
        )
        val testTarget = FlipperTarget.Received(target = "", sdk = SemVer(1, 0))
        flipperTargetFlow.emit(FlipperTarget.Received(target = "", sdk = SemVer(1, 0)))
        coEvery {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST1")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        } returns Result.success(listOf(getTestFapItemShort("TEST1", name = "From Internet")))

        underTest.refresh(force = true)

        var state = underTest.getLoadedFapsFlow().filter {
            it is FapInstalledInternalLoadingState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledInternalLoadingState.Loaded(
                faps = persistentListOf(
                    InstalledFapApp.OnlineFapApp(
                        getTestFapItemShort("TEST1", name = "From Internet")
                    ) to FapInstalledInternalState.Installed
                ),
                inProgress = true
            ),
            state
        )

        coEvery {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST2")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        } returns Result.success(listOf(getTestFapItemShort("TEST2", name = "From Internet")))

        uidsStateFlow.emit(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST1")
                        )
                    ),
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(
                            getTestManifest("TEST2")
                        )
                    )
                ),
                inProgress = true
            )
        )

        state = underTest.getLoadedFapsFlow().filter {
            it is FapInstalledInternalLoadingState.Loaded && it.faps.size == 2
        }.first()

        coVerify {
            fapNetworkApi.getAllItem(
                applicationIds = eq(listOf("TEST2")),
                offset = eq(0),
                limit = eq(1),
                sortType = any(),
                target = eq(testTarget)
            )
        }

        Assert.assertEquals(
            FapInstalledInternalLoadingState.Loaded(
                faps = persistentListOf(
                    InstalledFapApp.OnlineFapApp(
                        getTestFapItemShort("TEST1", name = "From Internet")
                    ) to FapInstalledInternalState.Installed,
                    InstalledFapApp.OnlineFapApp(
                        getTestFapItemShort("TEST2", name = "From Internet")
                    ) to FapInstalledInternalState.Installed
                ),
                inProgress = true
            ),
            state
        )
        childScope.cancel()
    }
}
