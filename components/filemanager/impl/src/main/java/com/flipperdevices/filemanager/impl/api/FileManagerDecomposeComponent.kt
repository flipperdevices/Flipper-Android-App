package com.flipperdevices.filemanager.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.impl.model.FileManagerNavigationConfig
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


interface FileManagerDecomposeComponent {
    val stack: Value<ChildStack<*, DecomposeComponent>>

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): FileManagerDecomposeComponent
    }
}

interface DecomposeComponent {
    @Composable
    fun Render()
}

class FileManagerDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val fileManagerListingFactory: FileManagerListingComponent.Factory,
    private val fileManagerUploadingFactory: FileManagerUploadingComponent.Factory,
    private val fileManagerEditingFactory: FileManagerEditingComponent.Factory,
    private val fileManagerDownloadFactory: FileManagerDownloadComponent.Factory
) : FileManagerDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FileManagerNavigationConfig>()

    override val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FileManagerNavigationConfig.serializer(),
        initialConfiguration = FileManagerNavigationConfig.Screen("/"),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FileManagerNavigationConfig, componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FileManagerNavigationConfig.Screen -> fileManagerListingFactory(
            componentContext, config, navigation
        )

        is FileManagerNavigationConfig.Uploading -> fileManagerUploadingFactory(
            componentContext, config, navigation
        )

        is FileManagerNavigationConfig.Editing -> fileManagerEditingFactory(
            componentContext, config, navigation
        )
        is FileManagerNavigationConfig.Download -> fileManagerDownloadFactory(
            componentContext, config, navigation
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, FileManagerDecomposeComponent.Factory::class)
    interface Factory : FileManagerDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
        ): FileManagerDecomposeComponentImpl
    }
}