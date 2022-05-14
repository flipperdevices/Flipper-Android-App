package com.flipperdevices.analytics.shake2report.impl

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.flipperdevices.analytics.shake2report.impl.activity.Shake2ReportActivity
import com.flipperdevices.analytics.shake2report.impl.listener.FlipperShakeDetector
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.preference.pb.Settings
import com.squareup.anvil.annotations.ContributesBinding
import fr.bipi.tressence.file.FileLoggerTree
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

private const val FILE_LOG_DIR = "log"
private const val FILE_LOG_SIZE = 1024 * 1024 // 1 MB
private const val FILE_LOG_LIMIT = 20
private const val VIBRATOR_TIME_MS = 500L

@Singleton
@ContributesBinding(AppGraph::class, InternalShake2Report::class)
class Shake2ReportImpl @Inject constructor(
    private val application: Application,
    private val dataStore: DataStore<Settings>
) : InternalShake2Report {
    private val alreadyRegistered = AtomicBoolean(false)

    private val shakeDetector by lazy {
        FlipperShakeDetector(application) {
            hearShake()
        }
    }
    private val vibrator by lazy {
        ContextCompat.getSystemService(
            application,
            Vibrator::class.java
        )
    }
    override val logDir: File by lazy { File(application.cacheDir, FILE_LOG_DIR) }
    private var screenshot: Bitmap? = null

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

        val shake2ReportEnabled = runBlocking { dataStore.data.first().shakeToReport }
        if (shake2ReportEnabled) {
            shakeDetector.register()
        }
    }

    override fun setExtra(tags: List<Pair<String, String>>) {
        Sentry.configureScope { scope ->
            tags.forEach {
                scope.setExtra(it.first, it.second)
            }
        }
    }

    override fun getScreenshotAndReset(): Bitmap? {
        val screenshotToReturn = screenshot
        screenshot = null
        return screenshotToReturn
    }

    private fun hearShake() {
        vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
        screenshot = takeScreenshot()

        application.startActivity(
            Intent(
                application,
                Shake2ReportActivity::class.java
            ).apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private fun takeScreenshot(): Bitmap? {
        val rootView = CurrentActivityHolder.getCurrentActivity()?.window?.decorView?.rootView
            ?: return null
        val width = rootView.width
        val height = rootView.height
        val screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        rootView.draw(Canvas(screenshot))
        return screenshot
    }
}
