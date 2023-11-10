package com.flipperdevices.share.receive.helpers

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.share.receive.R
import javax.inject.Inject


class ReceiveKeyActionHelper @Inject constructor(
    private val notificationStorage: InAppNotificationStorage,
    private val simpleKeyApi: SimpleKeyApi,
    private val keyParser: KeyParser,
    private val utilsKeyApi: UtilsKeyApi
) {
    suspend fun saveKey(key: FlipperKey): Result<Unit> = runCatching {
        simpleKeyApi.insertKey(key)
        notificationStorage.addNotification(
            InAppNotification.Successful(
                title = key.path.nameWithoutExtension,
                descId = R.string.saved_key_desc
            )
        )
    }

    private suspend fun findNewPathForKey(flipperKey: FlipperKey): FlipperKeyPath {
        return utilsKeyApi.findAvailablePath(flipperKey.getKeyPath())
    }

    private fun cloneKeyByPath(flipperKey: FlipperKey, newPath: FlipperKeyPath): FlipperKey {
        return flipperKey.copy(
            mainFile = flipperKey.mainFile.copy(
                path = newPath.path
            ),
            deleted = newPath.deleted
        )
    }

    suspend fun findNewPathAndCloneKey(flipperKey: FlipperKey): FlipperKey {
        val newPath = findNewPathForKey(flipperKey)
        return cloneKeyByPath(flipperKey, newPath)
    }

    suspend fun parseKey(flipperKey: FlipperKey): FlipperKeyParsed {
        return keyParser.parseKey(flipperKey)
    }
}
