package com.flipperdevices.selfupdater.thirdparty.api

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.BuildConfig
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterThirdParty @Inject constructor(
    context: Context,
    private val updateParser: SelfUpdateParserApi,
    private val inAppNotificationStorage: InAppNotificationStorage,
    private val applicationParams: ApplicationParams
) : SelfUpdaterApi, LogTagProvider {

    private val nameParser = updateParser.getName()
    override val TAG: String get() = "SelfUpdaterThirdParty"
    override fun getInstallSourceName() = "$nameParser/${BuildConfig.BUILD_TYPE}"

    private var downloadId: Long? = null
    private val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun startCheckUpdateAsync(activity: Activity) {
        if (activity !is LifecycleOwner) {
            error { "Activity must be LifecycleOwner" }
            return
        }

        (activity as LifecycleOwner).lifecycle.coroutineScope.launch {
            val lastRelease = processCheckGithubUpdate() ?: return@launch
            val notification = InAppNotification.UpdateReady(
                action = { downloadFile(lastRelease, activity) },
            )
            inAppNotificationStorage.addNotification(notification)
        }
    }

    private suspend fun processCheckGithubUpdate(): SelfUpdate? {
        val lastRelease = updateParser.getLastUpdate()
        if (lastRelease == null) {
            error { "No release found for github update" }
            return null
        }

        info { "Last update from Github $lastRelease" }

        val currentVersion = SemVer.fromString(applicationParams.version) ?: return null
        val newVersion = SemVer.fromString(lastRelease.version) ?: return null

        info { "Current version: $currentVersion && new version $newVersion" }

        if (currentVersion >= newVersion) {
            info { "Current version is up to date" }
            return null
        }

        return lastRelease
    }

    private fun registerDownloadReceiver(activity: Activity) {
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(downloadReceiver, intentFilter, Context.RECEIVER_EXPORTED) // check NOT
        } else {
            activity.registerReceiver(downloadReceiver, intentFilter)
        }
        info { "Register download receiver" }
    }

    private fun downloadFile(githubUpdate: SelfUpdate, activity: Activity) {
        try {
            val url = githubUpdate.downloadUrl
            val title = githubUpdate.name

            val networkTypes = DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            val request = DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(networkTypes)
                .setTitle(title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

            this.downloadId = manager.enqueue(request)
        } catch (e: Exception) {
            error { "Error while download update $e" }
        }
        registerDownloadReceiver(activity)
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intents: Intent) {
            try {
                val currentDownloadId = intents.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != currentDownloadId) {
                    return
                }

                val uri = manager.getUriForDownloadedFile(requireNotNull(downloadId))
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(intent)
                info { "Start install update" }
                context.unregisterReceiver(this)
            } catch (e: Exception) {
                error { "Error while receive update $e" }
            }
        }
    }
}
