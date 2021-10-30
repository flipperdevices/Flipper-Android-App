package com.flipperdevices.analytics.shake2report.activity

import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.analytics.shake2report.R
import com.flipperdevices.analytics.shake2report.Shake2Report
import com.flipperdevices.analytics.shake2report.Shake2ReportApi
import com.flipperdevices.analytics.shake2report.databinding.ActivityShake2reportBinding
import com.flipperdevices.analytics.shake2report.helper.Shake2ReportDialog
import io.sentry.Attachment
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod

private const val FILE_SCREENSHOT = "screenshot.png"
private const val QUALITY_SCREENSHOT = 95
private val SENTRY_TIMEOUT_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)

class Shake2ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShake2reportBinding
    private lateinit var shake2Report: Shake2Report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShake2reportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shake2Report = Shake2ReportApi.instance
            ?: Shake2ReportApi.initAndGet(applicationContext as Application)

        Shake2ReportDialog.show(this, onCancel = {
            finish()
        }, onSuccess = {
            startReportError()
        })
    }

    private fun startReportError() {
        lifecycleScope.launch {
            reportStatus(R.string.shake2report_activity_status_screenshot)
            saveScreenshotToLogFolder()
            reportStatus(R.string.shake2report_activity_status_zipping)
            val file = compressLogFolder()
            reportStatus(R.string.shake2report_activity_status_sending)
            sendingReport(file)
            finishInternal()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun compressLogFolder(): File = withContext(Dispatchers.IO) {
        val logDir = shake2Report.getLogDir()
        val zipFile = ZipFile(
            File(
                application.cacheDir,
                "logs-${System.currentTimeMillis()}.zip"
            )
        )
        val zipParameters = ZipParameters().apply {
            compressionMethod = CompressionMethod.DEFLATE
            compressionLevel = CompressionLevel.ULTRA
        }
        zipFile.addFolder(logDir, zipParameters)

        return@withContext zipFile.file
    }

    private suspend fun saveScreenshotToLogFolder() = withContext(Dispatchers.IO) {
        val logDir = shake2Report.getLogDir()
        val screenshot =
            Shake2ReportApi.instance?.getScreenshotAndReset() ?: return@withContext null
        val screenshotFile = File(logDir, FILE_SCREENSHOT)
        screenshotFile.delete()
        screenshotFile.outputStream().use { out ->
            screenshot.compress(Bitmap.CompressFormat.PNG, QUALITY_SCREENSHOT, out)
        }
    }

    private suspend fun sendingReport(logZip: File) = withContext(Dispatchers.IO) {
        val event = SentryEvent()
        event.level = SentryLevel.ERROR
        event.message =
            Message().apply { message = "Error via shake2report ${System.currentTimeMillis()}" }

        Sentry.withScope { scope ->
            scope.addAttachment(Attachment(logZip.absolutePath))
            Sentry.captureEvent(event)
            Sentry.flush(SENTRY_TIMEOUT_MS)
            scope.clearAttachments()
        }
    }

    private suspend fun reportStatus(@StringRes stringResId: Int) = withContext(Dispatchers.Main) {
        binding.status.setText(stringResId)
    }

    private suspend fun finishInternal() = withContext(Dispatchers.Main) {
        finish()
    }
}
