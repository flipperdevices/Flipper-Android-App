package com.flipperdevices.share.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.share.receive.di.KeyReceiveComponent
import com.flipperdevices.singleactivity.api.SingleActivityApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class ReferrerReceiver : BroadcastReceiver(), LogTagProvider {
    override val TAG: String = "ReferrerReceiver"

    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var singleActivityApi: SingleActivityApi

    @Inject
    lateinit var deepLinkParser: DeepLinkParser

    init {
        ComponentHolder.component<KeyReceiveComponent>().inject(this)
    }

    override fun onReceive(context: Context, intent: Intent?) {

        info { "Intent: $intent" }

//        val pendingResult: PendingResult = goAsync()
//        val uri = intent?.data ?: return
//        scope.launch(Dispatchers.Default) {
//            try {
//                val deeplink = deepLinkParser.fromUri(context, uri)
//                withContext(Dispatchers.Main) {
//                    singleActivityApi.open(deeplink)
//                }
//            } finally {
//                pendingResult.finish()
//            }
//        }
    }
}
