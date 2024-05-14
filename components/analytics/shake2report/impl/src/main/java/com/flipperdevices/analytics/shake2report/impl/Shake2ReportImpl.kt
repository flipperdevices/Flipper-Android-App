package com.flipperdevices.analytics.shake2report.impl

import android.app.Application
import android.util.Log
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import fr.bipi.treessence.file.FileLoggerTree
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val FILE_LOG_DIR = "log"
private const val FILE_LOG_SIZE = 1 * 1024 * 1024 // 1 MB
private const val FILE_LOG_LIMIT = 10

@Singleton
@ContributesBinding(AppGraph::class, InternalShake2Report::class)
class Shake2ReportImpl @Inject constructor(
    private val application: Application
) : InternalShake2Report {
    private val alreadyRegistered = MutableStateFlow(false)

    override val logDir: File by lazy { File(application.cacheDir, FILE_LOG_DIR) }

    override fun register() {
        if (alreadyRegistered.getAndUpdate { true }) {
            return
        }

        SentryAndroid.init(application) { options ->
            options.addIntegration(
                SentryTimberIntegration(
                    minEventLevel = SentryLevel.FATAL,
                    minBreadcrumbLevel = SentryLevel.DEBUG
                )
            )
        }

        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        val fileLoggerTree = FileLoggerTree.Builder()
            .withDir(logDir)
            .withFileName("timber-${System.currentTimeMillis()}-%g.log")
            .withSizeLimit(FILE_LOG_SIZE)
            .withFileLimit(FILE_LOG_LIMIT)
            .withMinPriority(Log.VERBOSE)
            .appendToFile(false)
            .build()
        Timber.plant(fileLoggerTree)
    }

    override fun setExtra(tags: List<Pair<String, String>>) {
        Sentry.configureScope { scope ->
            tags.forEach {
                scope.setExtra(it.first, it.second)
            }
        }
    }

    override fun getIsRegisteredFlow() = alreadyRegistered.asStateFlow()
}
