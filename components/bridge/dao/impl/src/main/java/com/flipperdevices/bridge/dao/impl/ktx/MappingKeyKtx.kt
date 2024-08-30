package com.flipperdevices.bridge.dao.impl.ktx

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.Key
import com.flipperdevices.bridge.dao.impl.model.SynchronizedStatus
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import kotlinx.collections.immutable.toImmutableList

internal fun FlipperKey.toDatabaseKey(): Key {
    return Key(
        path = path.pathToKey,
        type = path.keyType,
        content = DatabaseKeyContent(keyContent),
        notes = notes,
        synchronizedStatus = if (synchronized) {
            SynchronizedStatus.SYNCHRONIZED
        } else {
            SynchronizedStatus.NOT_SYNCHRONIZED
        },
        deleted = deleted
    )
}

internal suspend fun Key.toFlipperKey(additionalFileDao: AdditionalFileDao): FlipperKey {
    return FlipperKey(
        mainFile = FlipperFile(
            path = mainFilePath,
            content = content.flipperContent
        ),
        notes = notes,
        synchronized = synchronizedStatus == SynchronizedStatus.SYNCHRONIZED,
        deleted = deleted,
        additionalFiles = additionalFileDao.getFilesForKeyWithId(uid)
            .map {
                it.toFlipperFile()
            }.toImmutableList()
    )
}

internal fun Key.getFlipperKeyPath(): FlipperKeyPath {
    return FlipperKeyPath(
        path = mainFilePath,
        deleted = deleted
    )
}
