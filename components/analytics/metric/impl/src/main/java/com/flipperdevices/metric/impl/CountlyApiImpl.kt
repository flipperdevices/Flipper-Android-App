package com.flipperdevices.metric.impl

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ly.count.android.sdk.Countly
import ly.count.android.sdk.CountlyConfig

@Singleton
@ContributesBinding(AppGraph::class, CountlyApi::class)
class CountlyApiImpl @Inject constructor(
    private val application: Application,
    private val dataStore: DataStore<Settings>
) : CountlyApi, LogTagProvider {
    override val TAG = "CountlyApi"

    private val scope = CoroutineScope(SupervisorJob())

    private val countly by lazy { initCountly() }

    override fun reportEvent(
        id: String,
        params: Map<String, Any>?
    ) {
        scope.launch(Dispatchers.Default) {
            try {
                reportEventUnsafe(id, params)
            } catch (e: Exception) {
                error(e) { "Failed report event $id with $params" }
            }
        }
    }

    private fun reportEventUnsafe(
        id: String,
        params: Map<String, Any>?
    ) {
        verbose { "Report event $id with $params" }
        if (params == null) {
            countly.events().recordEvent(id)
        } else countly.events().recordEvent(id, params)
    }

    private fun initCountly(): Countly {
        val sharedInstance = Countly.sharedInstance()
        val config = CountlyConfig(
            application,
            BuildConfig.COUNTLY_APP_KEY,
            BuildConfig.COUNTLY_URL
        )
        config.setLoggingEnabled(BuildConfig.INTERNAL)
        val settings = runBlocking {
            dataStore.updateData {
                if (it.uuid.isNullOrBlank()) {
                    it.toBuilder()
                        .setUuid(UUID.randomUUID().toString())
                        .build()
                } else it
            }
        }
        info { "Init countly config with uuid ${settings.uuid}" }
        config.setDeviceId(settings.uuid)
        return sharedInstance.init(config)
    }
}
