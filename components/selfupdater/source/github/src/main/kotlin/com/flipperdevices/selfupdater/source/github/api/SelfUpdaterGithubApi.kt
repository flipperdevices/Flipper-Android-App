package com.flipperdevices.selfupdater.source.github.api

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.inappnotification.api.InAppNotificationStorage
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.selfupdater.api.SelfUpdaterApi
import com.flipperdevices.selfupdater.source.github.model.GithubUpdate
import com.flipperdevices.selfupdater.source.github.parser.CompareVersionParser
import com.flipperdevices.selfupdater.source.github.parser.GithubParser
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, SelfUpdaterApi::class)
class SelfUpdaterGithubApi @Inject constructor(
    private val context: Context,
    private val compareVersionParser: CompareVersionParser,
    private val githubReleaseParsers: MutableSet<GithubParser>,
    private val inAppNotificationStorage: InAppNotificationStorage
) : SelfUpdaterApi, LogTagProvider {
    override val TAG: String get() = "SelfUpdaterGithubApi"
    override fun getInstallSourceName() = "Github"

    private var downloadId: Long? = null
    private val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override suspend fun startCheckUpdateAsync(activity: Activity) {
        val lastRelease = processCheckGithubUpdate() ?: return
        val notification = InAppNotification.UpdateReady(
            action = { downloadFile(lastRelease) },
        )
        inAppNotificationStorage.addNotification(notification)
    }

    private suspend fun processCheckGithubUpdate(): GithubUpdate? {
        val githubParser = githubReleaseParsers.firstOrNull { it.isParserValid() }
        if (githubParser == null) {
            error { "No parser found for github update" }
            return null
        }

        val lastRelease = githubParser.getLastRelease()
        if (lastRelease == null) {
            error { "No release found for github update" }
            return null
        }

        info { "Choose github parser for update ${githubParser::javaClass} with $lastRelease" }

        if (!compareVersionParser.isThatNewVersion(lastRelease.version)) {
            info { "No new version found for github update" }
            return null
        }

        return lastRelease
    }

    private fun registerDownloadReceiver() {
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(downloadReceiver, intentFilter)
        }
        info { "Register download receiver" }
    }

    private fun downloadFile(githubUpdate: GithubUpdate) {
        val url = githubUpdate.downloadUrl
        val title = githubUpdate.name

        val networkTypes = DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(networkTypes)
            .setTitle(title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        this.downloadId = manager.enqueue(request)
        registerDownloadReceiver()
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
