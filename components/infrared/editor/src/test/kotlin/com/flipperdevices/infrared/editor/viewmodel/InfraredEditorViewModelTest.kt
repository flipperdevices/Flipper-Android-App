package com.flipperdevices.infrared.editor.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.infrared.InfraredRemote
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfraredEditorViewModelTest {
    private val flipperKeyPath = mockk<FlipperKeyPath>()
    private val flipperKey = mockk<FlipperKey>()

    private val keyParser = mockk<KeyParser>()
    private val simpleKeyApi = mockk<SimpleKeyApi>()
    private val updateKeyApi = mockk<UpdateKeyApi>(relaxUnitFun = true)
    private val synchronizationApi = mockk<SynchronizationApi>(relaxUnitFun = true)
    private val infraredEditorSaver = mockk<InfraredEditorSaver>()

    private val remotes = listOf(
        InfraredRemote.Parsed(nameInternal = "1", protocol = "", address = "", command = ""),
        InfraredRemote.Parsed(nameInternal = "2", protocol = "", address = "", command = ""),
    )
    private val keyName = "Flipper Name"

    private lateinit var viewModel: InfraredEditorViewModel

    @Before
    fun setup() {
        mockkStatic("kotlinx.coroutines.flow.FlowKt")
        val flipperKeyParsed = FlipperKeyParsed.Infrared(
            keyName = keyName,
            protocol = null,
            notes = null,
            remotes = remotes
        )

        coEvery { flipperKey.path.nameWithoutExtension } returns keyName
        coEvery { keyParser.parseKey(flipperKey) } returns flipperKeyParsed
        coEvery { simpleKeyApi.getKey(flipperKeyPath) } returns flipperKey

        viewModel = InfraredEditorViewModel(
            flipperKeyPath = flipperKeyPath,
            keyParser = keyParser,
            updateKeyApi = updateKeyApi,
            synchronizationApi = synchronizationApi,
            simpleKeyApi = simpleKeyApi,
            infraredEditorSaver = infraredEditorSaver
        )
    }

    @Test fun `Cancel edit without changes`() {
        val endAction: () -> Unit = mockk(relaxed = true)
        viewModel.onCancel(endAction)
        verify { endAction.invoke() }
    }

    @Test fun `Change position and try cancel edit with changes`() {
        viewModel.onChangePosition(0, 1)

        val endAction: () -> Unit = mockk(relaxed = true)
        viewModel.onCancel(endAction)
        Assert.assertEquals(viewModel.getShowOnSaveDialogState().value, true)
    }

    @Test fun `Save key with loading state`() {
        every { viewModel.getInfraredControlState().value } returns InfraredEditorState.Loading
        val endAction: () -> Unit = mockk(relaxed = true)
        viewModel.onSave(endAction)
        verify(atLeast = 0) { endAction() }
    }

    @Test fun `Save key with no changes`() {
        val endAction: () -> Unit = mockk(relaxed = true)
        viewModel.onSave(endAction)
        verify { updateKeyApi wasNot called }
        verify { endAction() }
    }

    @Test fun `Save key with changes`() {
        viewModel.onChangePosition(0, 1)

        val newFlipperKey = mockk<FlipperKey>()
        every { infraredEditorSaver.newFlipperKey(flipperKey, any()) } returns newFlipperKey

        val endAction: () -> Unit = mockk(relaxed = true)
        viewModel.onSave(endAction)
        coVerify { updateKeyApi.updateKey(flipperKey, newFlipperKey) }
        coVerify { synchronizationApi.startSynchronization(force = true) }
        verify { endAction() }
    }
}
