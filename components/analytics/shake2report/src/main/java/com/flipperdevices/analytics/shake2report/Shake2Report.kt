package com.flipperdevices.analytics.shake2report

import android.app.Application
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.squareup.seismic.ShakeDetector
import fr.bipi.tressence.file.FileLoggerTree
import java.io.File
import timber.log.Timber

private const val FILE_LOG_DIR = "log"
private const val FILE_SCREENSHOT = "screenshot.png"
private const val FILE_LOG_SIZE = 1024 * 1024 // 1 MB
private const val FILE_LOG_LIMIT = 20

object Shake2Report : ShakeDetector.Listener {
    private val shakeDetector = ShakeDetector(this)
    private var screenshotHelper: ScreenshotHelper? = null
    private var logDir: File? = null
    private var screenshotFile: File? = null

    fun register(application: Application) {
        screenshotHelper = ScreenshotHelper(application)
        logDir = File(application.cacheDir, FILE_LOG_DIR)
        if (!logDir!!.exists()) {
            logDir!!.mkdirs()
        }

        val fileLoggerTree = FileLoggerTree.Builder()
            .withDir(logDir!!)
            .withFileName("timber-${System.currentTimeMillis()}-%g.log")
            .withSizeLimit(FILE_LOG_SIZE)
            .withFileLimit(FILE_LOG_LIMIT)
            .withMinPriority(Log.VERBOSE)
            .appendToFile(false)
            .build()
        Timber.plant(fileLoggerTree)

        val sensorManager = ContextCompat.getSystemService(application, SensorManager::class.java)
        shakeDetector.start(sensorManager)

        screenshotHelper!!.register()
        screenshotFile = File(logDir, FILE_SCREENSHOT)
    }

    override fun hearShake() {
        if (screenshotFile == null) {
            Timber.wtf("Screenshot file is null")
            return
        }
        val bitmap = screenshotHelper?.takeScreenshot()
        if (bitmap == null) {
            Timber.e("Can't do screenshot")
        }
    }
}
