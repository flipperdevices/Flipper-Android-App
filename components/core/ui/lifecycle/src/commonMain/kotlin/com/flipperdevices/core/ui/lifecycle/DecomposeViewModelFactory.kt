package com.flipperdevices.core.ui.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlin.reflect.KClass

inline fun <reified VM : DecomposeViewModel> ComponentContext.viewModelWithFactoryWithoutRemember(
    key: Any?,
    crossinline factory: () -> VM
): VM {
    return instanceKeeper.getOrCreate(
        key = InstanceKey(clazz = VM::class, key = key),
        factory = factory
    )
}

@Composable
inline fun <reified VM : DecomposeViewModel> ComponentContext.viewModelWithFactory(
    key: Any?,
    crossinline factory: () -> VM
): VM {
    return remember(key, VM::class) {
        viewModelWithFactoryWithoutRemember(key, factory)
    }
}

data class InstanceKey(
    val clazz: KClass<*>,
    val key: Any?
)
