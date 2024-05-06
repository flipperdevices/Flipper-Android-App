package com.flipperdevices.faphub.installation.stateprovider.impl.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapBuildState
import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installation.stateprovider.api.model.NotAvailableReason
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

private val fapTestVersion = FapItemVersion(
    id = "",
    version = SemVer(0, 0),
    target = FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99)),
    buildState = FapBuildState.READY,
    sdkApi = null
)

private val fapActionRequestInstallMock = FapActionRequest.Install(
    applicationName = "",
    applicationUid = "",
    applicationAlias = "",
    toVersion = FapItemVersion(
        id = "",
        version = SemVer(0, 0),
        target = FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99)),
        buildState = FapBuildState.READY,
        sdkApi = null
    ),
    categoryAlias = "",
    iconUrl = ""
)

private val fapActionRequestUpdateMock = FapActionRequest.Update(
    applicationName = "",
    toVersion = FapItemVersion(
        id = "",
        version = SemVer(0, 0),
        target = FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99)),
        buildState = FapBuildState.READY,
        sdkApi = null
    ),
    iconUrl = "",
    from = FapManifestItem(
        applicationAlias = "",
        uid = "",
        versionUid = "",
        path = "",
        fullName = "",
        iconBase64 = null,
        sdkApi = null,
        sourceFileHash = null,
        isDevCatalog = false
    )
)

class FapInstallationStateManagerImplTest {
    private lateinit var underTest: FapInstallationStateManagerImpl
    private lateinit var fapManifestApi: FapManifestApi
    private lateinit var queueApi: FapInstallationQueueApi
    private lateinit var flipperTargetProviderApi: FlipperTargetProviderApi

    @Before
    fun setUp() {
        fapManifestApi = mockk {
            every { getManifestFlow() } returns MutableStateFlow(
                FapManifestState.Loaded(
                    items = persistentListOf(),
                    inProgress = false
                )
            )
        }
        queueApi = mockk {
            every { getFlowById(any()) } returns flowOf(FapQueueState.NotFound)
        }
        flipperTargetProviderApi = mockk {
            every { getFlipperTarget() } returns MutableStateFlow(null)
        }

        underTest = FapInstallationStateManagerImpl(
            fapManifestApi = fapManifestApi,
            queueApi = queueApi,
            flipperTargetProviderApi = flipperTargetProviderApi
        )
    }

    @Test
    fun `if state in flow exist return installation in progress`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(
            FapQueueState.InProgress(
                request = fapActionRequestInstallMock,
                float = 0.5f
            )
        )
        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.InstallationInProgress(
                active = true,
                progress = 0.5f
            ),
            fapState
        )
    }

    @Test
    fun `if state in flow exist return update in progress`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(
            FapQueueState.InProgress(
                request = fapActionRequestUpdateMock,
                float = 0.6f
            )
        )
        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.UpdatingInProgress(
                active = true,
                progress = 0.6f
            ),
            fapState
        )
    }

    @Test
    fun `if state in flow exist return pending update in progress`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(
            FapQueueState.Pending(
                request = fapActionRequestUpdateMock
            )
        )
        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.UpdatingInProgress(
                active = false,
                progress = 0f
            ),
            fapState
        )
    }

    @Test
    fun `if state in flow exist return pending installation in progress`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(
            FapQueueState.Pending(
                request = fapActionRequestInstallMock
            )
        )
        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.InstallationInProgress(
                active = false,
                progress = 0f
            ),
            fapState
        )
    }

    @Test
    fun `manifest retrieving when not target ready`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(null)

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.RetrievingManifest,
            fapState
        )
    }

    @Test
    fun `not connected when target not connected`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(FlipperTarget.NotConnected)

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.NotAvailableForInstall(NotAvailableReason.FLIPPER_NOT_CONNECTED),
            fapState
        )
    }

    @Test
    fun `unsupported when target not supported`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(FlipperTarget.Unsupported)

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.NotAvailableForInstall(
                NotAvailableReason.FLIPPER_OUTDATED
            ),
            fapState
        )
    }

    @Test
    fun `installed from manifest when build state not ready`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = null,
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.BUILD_RUNNING
            )
        ).first()

        Assert.assertEquals(
            FapState.Installed,
            fapState
        )
    }

    @Test
    fun `ready to update from manifest when update available`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = null,
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.READY,
                version = SemVer(1, 1)
            )
        ).first()

        Assert.assertEquals(
            FapState.ReadyToUpdate(
                FapManifestItem(
                    applicationAlias = "alias",
                    uid = "test-app",
                    versionUid = "",
                    path = "",
                    fullName = "",
                    iconBase64 = null,
                    sdkApi = null,
                    sourceFileHash = null,
                    isDevCatalog = false
                )
            ),
            fapState
        )
    }

    @Test
    fun `installed from manifest when sdk api same`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(32, 1))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = SemVer(32, 1),
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.READY
            )
        ).first()

        Assert.assertEquals(
            FapState.Installed,
            fapState
        )
    }

    @Test
    fun `installed from manifest when sdk api supported`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(32, 2))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = SemVer(32, 1),
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.READY
            )
        ).first()

        Assert.assertEquals(
            FapState.Installed,
            fapState
        )
    }

    @Test
    fun `ready to update from manifest is not supported`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(32, 1))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = SemVer(32, 2),
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.READY
            )
        ).first()

        Assert.assertEquals(
            FapState.ReadyToUpdate(
                FapManifestItem(
                    applicationAlias = "alias",
                    uid = "test-app",
                    versionUid = "",
                    path = "",
                    fullName = "",
                    iconBase64 = null,
                    sdkApi = SemVer(32, 2),
                    sourceFileHash = null,
                    isDevCatalog = false
                )
            ),
            fapState
        )
    }

    @Test
    fun `ready to update from manifest when manifest in old format`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(32, 1))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(
                    FapManifestItem(
                        applicationAlias = "alias",
                        uid = "test-app",
                        versionUid = "",
                        path = "",
                        fullName = "",
                        iconBase64 = null,
                        sdkApi = null,
                        sourceFileHash = null,
                        isDevCatalog = false
                    )
                ),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion.copy(
                buildState = FapBuildState.READY
            )
        ).first()

        Assert.assertEquals(
            FapState.ReadyToUpdate(
                FapManifestItem(
                    applicationAlias = "alias",
                    uid = "test-app",
                    versionUid = "",
                    path = "",
                    fullName = "",
                    iconBase64 = null,
                    sdkApi = null,
                    sourceFileHash = null,
                    isDevCatalog = false
                )
            ),
            fapState
        )
    }

    @Test
    fun `retrieving when manifest not ready`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(items = persistentListOf(), inProgress = true)
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.RetrievingManifest,
            fapState
        )
    }

    @Test
    fun `ready to install when not found in manifest`() = runTest {
        every { queueApi.getFlowById(eq("test-app")) } returns flowOf(FapQueueState.NotFound)
        every { flipperTargetProviderApi.getFlipperTarget() } returns MutableStateFlow(
            FlipperTarget.Received(target = "fTest", sdk = SemVer(99, 99))
        )
        every { fapManifestApi.getManifestFlow() } returns MutableStateFlow(
            FapManifestState.Loaded(
                items = persistentListOf(),
                inProgress = false
            )
        )

        val fapState = underTest.getFapStateFlow(
            applicationUid = "test-app",
            currentVersion = fapTestVersion
        ).first()

        Assert.assertEquals(
            FapState.ReadyToInstall,
            fapState
        )
    }
}
