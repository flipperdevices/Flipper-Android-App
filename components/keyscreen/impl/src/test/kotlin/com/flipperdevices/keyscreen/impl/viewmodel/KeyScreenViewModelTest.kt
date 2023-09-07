package com.flipperdevices.keyscreen.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class KeyScreenViewModelTest {

    private val flipperKeyPath: FlipperKeyPath = mockk()
    private val keyStateHelperApi: KeyStateHelperApi.Builder = mockk()
    private val keyStateHelper: KeyStateHelperApi = mockk(relaxUnitFun = true)
    private val metricApi: MetricApi = mockk(relaxUnitFun = true)

    private lateinit var underTest: KeyScreenViewModel

    @Before fun setUp() {
        every { keyStateHelperApi.build(any(), any()) } returns keyStateHelper

        underTest = KeyScreenViewModel(
            flipperKeyPath,
            keyStateHelperApi,
            metricApi
        )
    }

    @Test fun `get current key state`() {
        val expectedState = MutableStateFlow(KeyScreenState.InProgress)
        every { keyStateHelper.getKeyScreenState() } returns expectedState

        val actualState = underTest.getKeyScreenState()
        verify { keyStateHelper.getKeyScreenState() }
        Assert.assertEquals(expectedState, actualState)
    }

    @Test fun `set favorite`() {
        val isFavorite = true
        val slot = slot<Boolean>()

        underTest.setFavorite(isFavorite)

        verify { keyStateHelper.setFavorite(capture(slot)) }
        Assert.assertEquals(isFavorite, slot.captured)
    }

    @Test fun `open edit key`() {
        val onEndAction: (FlipperKeyPath) -> Unit = mockk(relaxUnitFun = true)
        val slot = slot<(FlipperKeyPath) -> Unit>()

        underTest.onOpenEdit(onEndAction)

        verify { keyStateHelper.onOpenEdit(capture(slot)) }
        Assert.assertEquals(onEndAction, slot.captured)
    }

    @Test fun `delete key`() {
        val onEndAction: () -> Unit = mockk(relaxUnitFun = true)
        val slot = slot<() -> Unit>()

        underTest.onDelete(onEndAction)

        verify { keyStateHelper.onDelete(capture(slot)) }
        Assert.assertEquals(onEndAction, slot.captured)
    }

    @Test fun `restore key`() {
        val onEndAction: () -> Unit = mockk(relaxUnitFun = true)
        val slot = slot<() -> Unit>()

        underTest.onRestore(onEndAction)

        verify { keyStateHelper.onRestore(capture(slot)) }
        Assert.assertEquals(onEndAction, slot.captured)
    }

    @Test fun `open nfc editor with incorrect state`() {
        every { keyStateHelper.getKeyScreenState() } returns MutableStateFlow(KeyScreenState.InProgress)
        val onEndAction: (FlipperKeyPath) -> Unit = mockk(relaxUnitFun = true)

        underTest.openNfcEditor(onEndAction)
        verify(exactly = 0) { keyStateHelper.onOpenEdit(any()) }
        verify(exactly = 0) { metricApi.reportSimpleEvent(SimpleEvent.OPEN_NFC_DUMP_EDITOR) }
    }

    @Test
    fun `open nfc editor with correct state`() {
        val flipperKey = mockk<FlipperKey>()
        val flipperKeyPath = mockk<FlipperKeyPath>()
        every { flipperKey.getKeyPath() } returns flipperKeyPath

        val expectedState = MutableStateFlow(
            KeyScreenState.Ready(
                parsedKey = mockk(),
                favoriteState = mockk(),
                shareState = mockk(),
                deleteState = mockk(),
                flipperKey = flipperKey,
                emulateConfig = mockk()
            )
        )

        every { keyStateHelper.getKeyScreenState() } returns expectedState

        val onEndAction: (FlipperKeyPath) -> Unit = mockk(relaxUnitFun = true)
        every { onEndAction.invoke(any()) } just Runs

        underTest.openNfcEditor(onEndAction)

        verify { metricApi.reportSimpleEvent(SimpleEvent.OPEN_NFC_DUMP_EDITOR) }
        verify { onEndAction(flipperKeyPath) }
    }
}
