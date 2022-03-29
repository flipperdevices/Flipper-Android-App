package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus

internal fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        path = path,
        content = DatabaseKeyContent(keyContent),
        notes = notes
    )
}

internal fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        path = path,
        keyContent = content.flipperContent,
        notes = notes,
        synchronized = synchronizedStatus == SynchronizedStatus.SYNCHRONIZED
    )
}
