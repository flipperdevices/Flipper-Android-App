package com.flipperdevices.wearable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissKeys
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet

// Copy from https://github.com/joreilly/Confetti

/**
 * Displays the [ChildStack] in [SwipeToDismissBox][androidx.wear.compose.material.SwipeToDismissBox].
 *
 * @param stack a [ChildStack] to be displayed.
 * @param onDismiss called when the swipe to dismiss gesture has completed, allows popping the stack.
 * See [StackNavigator#pop][com.arkivanov.decompose.router.stack.pop].
 * @param modifier a [Modifier] to be applied to the underlying
 * [SwipeToDismissBox][androidx.wear.compose.material.SwipeToDismissBox].
 * @param content a Composable slot displaying a [Child][Child.Created].
 */
@Composable
fun <C : Any, T : Any> SwipeToDismissBox(
    stack: Value<ChildStack<C, T>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
    val state = stack.subscribeAsState()

    SwipeToDismissBox(
        stack = state.value,
        onDismiss = onDismiss,
        modifier = modifier,
        content = content
    )
}

/**
 * Displays the [ChildStack] in [SwipeToDismissBox][androidx.wear.compose.material.SwipeToDismissBox].
 *
 * @param stack a [ChildStack] to be displayed.
 * @param onDismiss called when the swipe to dismiss gesture has completed, allows popping the stack.
 * See [StackNavigator#pop][com.arkivanov.decompose.router.stack.pop].
 * @param modifier a [Modifier] to be applied to the underlying
 * [SwipeToDismissBox][androidx.wear.compose.material.SwipeToDismissBox].
 * @param content a Composable slot displaying a [Child][Child.Created].
 */
@Composable
fun <C : Any, T : Any> SwipeToDismissBox(
    stack: ChildStack<C, T>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
    val active: Child.Created<C, T> = stack.active
    val background: Child.Created<C, T>? = stack.backStack.lastOrNull()
    val holder = rememberSaveableStateHolder()

    RetainStates(holder, stack.getConfigurations().toImmutableSet())

    SwipeToDismissBox(
        onDismissed = onDismiss,
        modifier = modifier,
        backgroundKey = background?.configuration ?: SwipeToDismissKeys.Background,
        contentKey = active.configuration,
        hasBackground = background != null,
    ) { isBackground ->
        val child = background?.takeIf { isBackground } ?: active
        holder.SaveableStateProvider(child.configuration.key()) {
            content(child)
        }
    }
}

private fun ChildStack<*, *>.getConfigurations(): Set<String> =
    items.mapTo(HashSet()) { it.configuration.key() }

private fun Any.key(): String = "${this::class.simpleName}_${hashCode().toString(radix = 36)}"

@Composable
private fun RetainStates(holder: SaveableStateHolder, currentKeys: ImmutableSet<String>) {
    val keys = remember(holder) { Keys(currentKeys) }

    DisposableEffect(holder, currentKeys) {
        keys.set.forEach {
            if (it !in currentKeys) {
                holder.removeState(it)
            }
        }

        keys.set = currentKeys

        onDispose {}
    }
}

private class Keys(var set: Set<String>)
