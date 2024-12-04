package com.flipperdevices.filemanager.editor.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.filemanager.editor.composable.FileManagerEditorComposable
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable
import okio.Path

@Suppress("LongParameterList")
class EditorDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
    @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
    @Assisted private val editFinishedCallback: EditFinishedCallback,
    editorViewModelFactory: EditorViewModel.Factory,
    editFileNameDecomposeComponentFactory: EditFileNameDecomposeComponent.Factory
) : ScreenDecomposeComponent(componentContext) {

    private val editorViewModel = instanceKeeper.getOrCreate {
        editorViewModelFactory.invoke(
            fullPathOnFlipper = fullPathOnFlipper,
            fullPathOnDevice = fullPathOnDevice
        )
    }

    private fun saveFile() {
        editorViewModel.writeNow()
        editFinishedCallback.invoke(
            fullPathOnFlipper = editorViewModel.state.value.fullPathOnFlipper
        )
    }

    @Serializable
    sealed interface SlotConfiguration {
        data object ChangeFlipperFileName : SlotConfiguration
    }

    private val slotNavigation = SlotNavigation<SlotConfiguration>()

    private val fileOptionsSlot = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        serializer = SlotConfiguration.serializer(),
        childFactory = { config, childContext ->
            when (config) {
                SlotConfiguration.ChangeFlipperFileName -> {
                    editFileNameDecomposeComponentFactory.invoke(
                        componentContext = childContext,
                        fullPathOnFlipper = editorViewModel.state.value.fullPathOnFlipper,
                        onBack = slotNavigation::dismiss,
                        onChanged = { fullPathOnFlipper ->
                            editorViewModel.onFlipperPathChanged(fullPathOnFlipper)
                            slotNavigation.dismiss()
                            saveFile()
                        }
                    )
                }
            }
        }
    )

    @Composable
    override fun Render() {
        FileManagerEditorComposable(
            editorViewModel = editorViewModel,
            onBack = onBack::invoke,
            onSaveAsClick = {
                slotNavigation.activate(SlotConfiguration.ChangeFlipperFileName)
            },
            onSaveClick = { saveFile() }
        )
        fileOptionsSlot.subscribeAsState().value.child?.instance?.Render()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            @Assisted("fullPathOnDevice") fullPathOnDevice: Path,
            editFinishedCallback: EditFinishedCallback
        ): EditorDecomposeComponent
    }

    fun interface EditFinishedCallback {
        fun invoke(fullPathOnFlipper: Path)
    }
}
