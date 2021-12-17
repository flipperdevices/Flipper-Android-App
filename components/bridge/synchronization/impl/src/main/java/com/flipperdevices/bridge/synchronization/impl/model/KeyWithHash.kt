package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.serialization.Serializable

@Serializable
data class KeyWithHash(
    val keyPath: KeyPath,
    val hash: String
)

/**
 * @return path to key with hash map
 */
fun List<KeyWithHash>.toHashMap(): HashMap<String, KeyWithHash> {
    return this.map {
        it.keyPath.path to it
    }.toMap(HashMap(this.size))
}
