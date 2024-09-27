package com.flipperdevices.filemanager.main.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.main.api.FileManagerDecomposeComponent
import com.flipperdevices.filemanager.main.impl.model.FileManagerNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, FileManagerDecomposeComponent.Factory::class)
class RemoteControlsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
) : FileManagerDecomposeComponent<FileManagerNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack = childStack(
        source = navigation,
        serializer = FileManagerNavigationConfig.serializer(),
        initialConfiguration = FileManagerNavigationConfig.DefaultFileTree,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FileManagerNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FileManagerNavigationConfig.FileTree -> {
            TODO()
        }
    }
}
