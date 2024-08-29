package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("LongMethod")
class InstalledFapsUidsProducerTest {
    private lateinit var fapManifestStateFlow: MutableStateFlow<FapManifestState>
    private lateinit var fapQueueStateFlow: MutableStateFlow<ImmutableList<FapQueueState>>
    private lateinit var underTest: InstalledFapsUidsProducer

    @Before
    fun setUp() {
        fapManifestStateFlow = MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(),
                inProgress = true
            )
        )
        fapQueueStateFlow = MutableStateFlow(
            persistentListOf()
        )

        val fapManifestApi = mockk<FapManifestApi>(relaxUnitFun = true) {
            every { getManifestFlow() } returns fapManifestStateFlow
        }
        underTest = InstalledFapsUidsProducer(
            queueApi = mockk(relaxUnitFun = true) {
                every { getAllTasks() } returns fapQueueStateFlow
            },
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

        val job = launch {
            underTest.refresh(this, force = true)
        }

        val state = underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                    )
                ),
                inProgress = true
            ),
            state
        )
        job.cancelAndJoin()
    }

    @Test
    fun `reinvalidate don't refresh cache`() = runTest(UnconfinedTestDispatcher()) {
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST")),
                inProgress = true
            )
        )

        val states = mutableListOf<FapInstalledUidsState>()
        val stateSubscribeJob = launch {
            underTest.getUidsStateFlow().collect {
                states.add(it)
            }
        }

        var job = launch {
            underTest.refresh(this, force = true)
        }
        underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertArrayEquals(
            arrayOf(
                FapInstalledUidsState.Loaded(faps = persistentListOf(), inProgress = true),
                FapInstalledUidsState.Loaded(
                    faps = persistentListOf(
                        FapInstalledFromManifest.Offline(
                            InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                        )
                    ),
                    inProgress = true
                )
            ),
            states.toTypedArray()
        )

        job.cancelAndJoin()

        job = launch {
            underTest.refresh(this, force = false)
        }
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST2")),
                inProgress = false
            )
        )
        underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.find { it.applicationUid == "TEST2" } != null
        }.first()

        Assert.assertArrayEquals(
            arrayOf(
                FapInstalledUidsState.Loaded(faps = persistentListOf(), inProgress = true),
                FapInstalledUidsState.Loaded(
                    faps = persistentListOf(
                        FapInstalledFromManifest.Offline(
                            InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                        )
                    ),
                    inProgress = true
                ),
                FapInstalledUidsState.Loaded(
                    faps = persistentListOf(
                        FapInstalledFromManifest.Offline(
                            InstalledFapApp.OfflineFapApp(getTestManifest("TEST2"))
                        )
                    ),
                    inProgress = false
                )
            ),
            states.toTypedArray()
        )

        job.cancelAndJoin()
        stateSubscribeJob.cancelAndJoin()
    }

    @Test
    fun `support queue api items in progress`() = runTest {
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST")),
                inProgress = true
            )
        )
        fapQueueStateFlow.emit(
            persistentListOf(
                FapQueueState.InProgress(
                    request = mockk {
                        every { applicationUid } returns "TEST_UID_QUEUE"
                    },
                    float = 0.5f
                )
            )
        )

        val job = launch {
            underTest.refresh(this, force = true)
        }

        val state = underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                    ),
                    FapInstalledFromManifest.RawUid(
                        applicationUid = "TEST_UID_QUEUE"
                    )
                ),
                inProgress = true
            ),
            state
        )
        job.cancelAndJoin()
    }

    @Test
    fun `support queue api items pending`() = runTest {
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST")),
                inProgress = true
            )
        )
        fapQueueStateFlow.emit(
            persistentListOf(
                FapQueueState.Pending(
                    request = mockk {
                        every { applicationUid } returns "TEST_UID_QUEUE_PENDING"
                    }
                )
            )
        )

        val job = launch {
            underTest.refresh(this, force = true)
        }

        val state = underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                    ),
                    FapInstalledFromManifest.RawUid(
                        applicationUid = "TEST_UID_QUEUE_PENDING"
                    )
                ),
                inProgress = true
            ),
            state
        )
        job.cancelAndJoin()
    }

    @Test
    fun `higher priority to manifest item with equals uid`() = runTest {
        fapManifestStateFlow.emit(
            FapManifestState.Loaded(
                items = persistentListOf(getTestManifest("TEST")),
                inProgress = true
            )
        )
        fapQueueStateFlow.emit(
            persistentListOf(
                FapQueueState.InProgress(
                    request = mockk {
                        every { applicationUid } returns "TEST_UID_QUEUE_INPROGRESS"
                    },
                    float = 0.5f
                ),
                FapQueueState.InProgress(
                    request = mockk {
                        every { applicationUid } returns "TEST"
                    },
                    float = 0.5f
                )
            )
        )

        val job = launch {
            underTest.refresh(this, force = true)
        }

        val state = underTest.getUidsStateFlow().filter {
            it is FapInstalledUidsState.Loaded && it.faps.isNotEmpty()
        }.first()

        Assert.assertEquals(
            FapInstalledUidsState.Loaded(
                faps = persistentListOf(
                    FapInstalledFromManifest.Offline(
                        InstalledFapApp.OfflineFapApp(getTestManifest("TEST"))
                    ),
                    FapInstalledFromManifest.RawUid(
                        applicationUid = "TEST_UID_QUEUE_INPROGRESS"
                    )
                ),
                inProgress = true
            ),
            state
        )
        job.cancelAndJoin()
    }
}
