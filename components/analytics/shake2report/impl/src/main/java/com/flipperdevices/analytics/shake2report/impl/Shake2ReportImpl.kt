package com.flipperdevices.analytics.shake2report.impl

import android.app.Application
import android.util.Log
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import fr.bipi.tressence.file.FileLoggerTree
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
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
    private val alreadyRegistered = AtomicBoolean(false)

    override val logDir: File by lazy { File(application.cacheDir, FILE_LOG_DIR) }

    override fun register() {
        if (!alreadyRegistered.compareAndSet(false, true)) {
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
}
