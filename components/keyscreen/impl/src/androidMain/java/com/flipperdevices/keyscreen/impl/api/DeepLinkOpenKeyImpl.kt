package com.flipperdevices.keyscreen.impl.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

private const val EXTRA_PATH_KEY = "key_path"
private const val EXTRA_IS_DELETED_KEY = "is_delete"

@ContributesBinding(AppGraph::class, DeepLinkOpenKey::class)
class DeepLinkOpenKeyImpl @Inject constructor(
    private val context: Context,
    private val applicationParams: ApplicationParams
) : DeepLinkOpenKey {

    override fun getIntentForOpenKey(keyPath: FlipperKeyPath): Intent {
        val intent = Intent(context, applicationParams.startApplicationClass.java)
        intent.putExtra(EXTRA_PATH_KEY, keyPath.path.pathToKey)
        intent.putExtra(EXTRA_IS_DELETED_KEY, keyPath.deleted)
        return intent
    }
}
