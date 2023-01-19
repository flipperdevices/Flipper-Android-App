package com.flipperdevices.core.ui.hexkeyboard

import java.util.EnumMap

class ImmutableEnumMap<K, V>(keyType: Class<K>) : EnumMap<K, V>(keyType) where K : Enum<K> {
    override operator fun get(key: K): V {
        return requireNotNull(super.get(key)) { "We cant get null value" }
    }

    operator fun set(key: K, value: V) {
        throw IllegalAccessError("We cant set value in immutable enum map")
    }

    constructor(keyType: Class<K>, items: Array<K>, action: (K) -> V) : this(keyType) {
        items.forEach { put(it, action(it)) }
    }
}
