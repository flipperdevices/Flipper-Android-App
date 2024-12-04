package com.flipperdevices.filemanager.editor.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.filemanager.editor.composable.dialog.CreateFileDialogComposable
import com.flipperdevices.filemanager.editor.viewmodel.EditFileNameViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okio.Path

class EditFileNameDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted("fullPathOnFlipper") private val fullPathOnFlipper: Path,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val onChanged: (Path) -> Unit,
    editFileNameViewModelFactory: EditFileNameViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {
    private val editFileNameViewModel = instanceKeeper.getOrCreate {
        editFileNameViewModelFactory.invoke(fullPathOnFlipper)
    }

    @Composable
    override fun Render() {
        CreateFileDialogComposable(
            editFileNameViewModel = editFileNameViewModel,
            onFinish = { name ->
                fullPathOnFlipper.parent?.resolve(name)?.run(onChanged)
            },
            onDismiss = onBack::invoke
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("fullPathOnFlipper") fullPathOnFlipper: Path,
            onBack: DecomposeOnBackParameter,
            onChanged: (Path) -> Unit
        ): EditFileNameDecomposeComponent
    }
}
