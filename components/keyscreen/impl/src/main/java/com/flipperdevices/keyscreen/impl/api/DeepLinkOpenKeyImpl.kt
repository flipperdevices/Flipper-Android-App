package com.flipperdevices.keyscreen.impl.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import java.io.File
import javax.inject.Inject

private const val EXTRA_PATH_KEY = "key_path"
private const val EXTRA_IS_DELETED_KEY = "is_delete"

@ContributesBinding(AppGraph::class, DeepLinkOpenKey::class)
@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkOpenKeyImpl @Inject constructor(
    private val context: Context,
    private val applicationParams: ApplicationParams
) : DeepLinkParserDelegate, DeepLinkOpenKey, LogTagProvider {
    override val TAG = "DeepLinkOpenKey"
    override fun getPriority(context: Context, intent: Intent): DeepLinkParserDelegatePriority? {
        return if (intent.hasExtra(EXTRA_PATH_KEY)) {
            DeepLinkParserDelegatePriority.HIGH
        } else {
            null
        }
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val path = intent.getStringExtra(EXTRA_PATH_KEY) ?: return null
        val isDeleted = intent.getBooleanExtra(EXTRA_IS_DELETED_KEY, false)
        val pathFile = File(path)
        val keyPath = FlipperKeyPath(
            path = FlipperFilePath(
                pathFile.absoluteFile.parentFile?.absolutePath ?: "",
                pathFile.name
            ),
            deleted = isDeleted
        )

        return Deeplink.OpenKey(keyPath)
    }

    override fun getIntentForOpenKey(keyPath: FlipperKeyPath): Intent {
        val intent = Intent(context, applicationParams.startApplicationClass.java)
        intent.putExtra(EXTRA_PATH_KEY, keyPath.path.pathToKey)
        intent.putExtra(EXTRA_IS_DELETED_KEY, keyPath.deleted)
        return intent
    }
}
