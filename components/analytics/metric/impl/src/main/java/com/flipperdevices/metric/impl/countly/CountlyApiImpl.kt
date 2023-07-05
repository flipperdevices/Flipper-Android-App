package com.flipperdevices.metric.impl.countly

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toIntSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.impl.BuildConfig
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ly.count.android.sdk.Countly
import ly.count.android.sdk.CountlyConfig
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

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
        params: Map<String, Any?>?
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
        params: Map<String, Any?>?
    ) {
        verbose { "Report event $id with $params" }
        if (params == null) {
            countly.events().recordEvent(id)
        } else {
            countly.events().recordEvent(id, filterParams(params))
        }
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
                } else {
                    it
                }
            }
        }
        info { "Init countly config with uuid ${settings.uuid} and ${BuildConfig.COUNTLY_URL}" }
        config.setDeviceId(settings.uuid)
        return sharedInstance.init(config)
    }
}

private fun filterParams(params: Map<String, Any?>): Map<String, Any?> {
    return params.mapValues {
        return@mapValues when (val mapValue = it.value) {
            is String,
            is Int,
            is Double,
            is Boolean -> mapValue
            is Long -> mapValue.toIntSafe()
            null -> null
            else -> mapValue.toString()
        }
    }
}
