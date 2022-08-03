package com.flipperdevices.core.ui.hexkeyboard

import java.util.EnumMap

class ImmutableEnumMap<K, V>(keyType: Class<K>) : EnumMap<K, V>(keyType) where K : Enum<K> {
    override operator fun get(key: K): V {
        return super.get(key) ?: throw RuntimeException("We cant get null value")
    }
    operator fun set(key: K, value: V) {
        throw RuntimeException("We cant set value in immutable enum map")
    }
    constructor(keyType: Class<K>, items: Array<K>, action: () -> V) : this(keyType) {
        items.forEach { put(it, action()) }
    }
}
