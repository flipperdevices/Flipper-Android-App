package com.flipperdevices.core.data

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

@Stable
class PredefinedEnumMap<Key : Enum<Key>, Value>(
    type: Class<Key>,
    action: (Key) -> Value
) {
    private val values: Array<Key> = type.enumConstants as Array<Key>
    private val map: ImmutableMap<Key, Value> = values.associateWith(action).toImmutableMap()
    operator fun get(key: Key): Value {
        return requireNotNull(map[key]) { "Key $key not found" }
    }
}
