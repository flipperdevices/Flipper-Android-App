package com.flipperdevices.ui.decompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

abstract class CompositeDecomposeComponent<C : Serializable>(
    componentContext: ComponentContext,
    serializer: KSerializer<C>,
    initialStack: () -> List<C>,
    isHandleBackButton: Boolean = true
) : DecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<C>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = serializer,
        initialStack = initialStack,
        handleBackButton = isHandleBackButton,
        childFactory = ::child,
    )

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val childStack by stack.subscribeAsState()

        Children(
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }

    abstract fun child(
        config: C,
        componentContext: ComponentContext
    ): DecomposeComponent
}