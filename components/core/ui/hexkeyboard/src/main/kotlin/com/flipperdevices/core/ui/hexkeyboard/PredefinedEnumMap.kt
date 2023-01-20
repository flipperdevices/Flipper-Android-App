package com.flipperdevices.core.ui.hexkeyboard

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

@Stable
class PredefinedEnumMap<Key, Value>(
    items: Array<Key>,
    action: (Key) -> Value
) where Key : Enum<Key> {

    private val map: ImmutableMap<Key, Value> = items.associateWith(action).toImmutableMap()
    operator fun get(key: Key): Value {
        return requireNotNull(map[key]) { "Key $key not found" }
    }
}
