package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus

internal fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        path = path.pathToKey,
        type = path.fileType,
        content = DatabaseKeyContent(keyContent),
        notes = notes,
        synchronizedStatus = if (synchronized) SynchronizedStatus.SYNCHRONIZED
        else SynchronizedStatus.NOT_SYNCHRONIZED,
        deleted = path.deleted
    )
}

internal fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        path = keyPath,
        keyContent = content.flipperContent,
        notes = notes,
        synchronized = synchronizedStatus == SynchronizedStatus.SYNCHRONIZED
    )
}
