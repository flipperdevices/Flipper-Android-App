package com.flipperdevices.newfilemanager.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.newfilemanager.api.navigation.FileManagerDecomposeComponent
import com.flipperdevices.newfilemanager.impl.model.FileManagerNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FileManagerDecomposeComponent.Factory::class)
class FileManagerDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fileManagerListingFactory: FileManagerListingComponent.Factory,
    private val fileManagerUploadingFactory: FileManagerUploadingComponent.Factory,
    private val fileManagerEditingFactory: FileManagerEditingComponent.Factory,
    private val fileManagerDownloadFactory: FileManagerDownloadComponent.Factory
) : FileManagerDecomposeComponent<FileManagerNavigationConfig>(),
    ComponentContext by componentContext {

    override val stack: Value<ChildStack<FileManagerNavigationConfig, DecomposeComponent>> =
        childStack(
            source = navigation,
            serializer = FileManagerNavigationConfig.serializer(),
            initialConfiguration = FileManagerNavigationConfig.Screen("/"),
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: FileManagerNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FileManagerNavigationConfig.Screen -> fileManagerListingFactory(
            componentContext,
            config,
            navigation
        )

        is FileManagerNavigationConfig.Uploading -> fileManagerUploadingFactory(
            componentContext,
            config,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FileManagerNavigationConfig.Editing -> fileManagerEditingFactory(
            componentContext,
            config,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FileManagerNavigationConfig.Download -> fileManagerDownloadFactory(
            componentContext,
            config,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
