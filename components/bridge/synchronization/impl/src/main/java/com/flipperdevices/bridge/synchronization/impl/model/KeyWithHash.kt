package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import kotlinx.serialization.Serializable

@Serializable
data class KeyWithHash(
    val keyPath: FlipperKeyPath,
    val hash: String
)

/**
 * @return path to key with hash map
 */
fun List<KeyWithHash>.toLinkedHashMap(): LinkedHashMap<String, KeyWithHash> {
    return this.map {
        it.keyPath.pathToKey to it
    }.toMap(LinkedHashMap(this.size))
}
