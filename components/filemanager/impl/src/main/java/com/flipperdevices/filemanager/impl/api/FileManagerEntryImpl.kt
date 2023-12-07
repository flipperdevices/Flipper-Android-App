package com.flipperdevices.filemanager.impl.api

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.filemanager.api.navigation.FileManagerEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileManagerEntry::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class FileManagerEntryImpl @Inject constructor(
    private val fileManagerFactory: FileManagerDecomposeComponent.Factory
) : FileManagerEntry {

    override fun fileManagerDestination() = "@${ROUTE.name}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(
            route = "@${ROUTE.name}"
        ) {
            val componentContext = rememberComponentContext()
            val fileManagerComponent = remember(componentContext) {
                fileManagerFactory(componentContext)
            }
            val childStack by fileManagerComponent.stack.subscribeAsState()

            Children(
                stack = childStack,
                animation = stackAnimation { _, _, direction ->
                    if (direction.isFront) {
                        slide() + fade()
                    } else {
                        scale(frontFactor = 1F, backFactor = 0.7F) + fade()
                    }
                }
            ) {
                it.instance.Render()
            }
        }
    }
}

@Composable
fun rememberComponentContext(): ComponentContext {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val savedStateRegistry = LocalSavedStateRegistryOwner.current.savedStateRegistry
    val viewModelStore = LocalViewModelStoreOwner.current?.viewModelStore
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    return remember(lifecycle, savedStateRegistry, viewModelStore, onBackPressedDispatcher) {
        DefaultComponentContext(
            lifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            viewModelStore = viewModelStore,
            onBackPressedDispatcher = onBackPressedDispatcher
        )
    }
}
