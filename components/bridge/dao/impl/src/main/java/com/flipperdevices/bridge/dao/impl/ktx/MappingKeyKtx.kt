package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus

internal fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        path = path.pathToKey,
        type = path.keyType,
        content = DatabaseKeyContent(keyContent),
        notes = notes,
        synchronizedStatus = if (synchronized) SynchronizedStatus.SYNCHRONIZED
        else SynchronizedStatus.NOT_SYNCHRONIZED,
        deleted = deleted
    )
}

internal fun Key.toFlipperKey(): FlipperKey {
    return FlipperKey(
        mainFile = FlipperFile(
            path = mainFilePath,
            content = content.flipperContent
        ),
        notes = notes,
        synchronized = synchronizedStatus == SynchronizedStatus.SYNCHRONIZED,
        deleted = deleted
    )
}
