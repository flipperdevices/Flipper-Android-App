package com.flipperdevices.analytics.shake2report.impl.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.analytics.shake2report.impl.Shake2Report
import com.flipperdevices.analytics.shake2report.impl.databinding.ActivityShake2reportBinding
import com.flipperdevices.analytics.shake2report.impl.di.Shake2ReportComponent
import com.flipperdevices.analytics.shake2report.impl.helper.Shake2ReportDialog
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toast
import io.sentry.Attachment
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import io.sentry.protocol.SentryId
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
        ComponentHolder.component<Shake2ReportComponent>().inject(this)

        setContentView(binding.root)
        // shake2Report = shake2ReportApi.instance
        //    ?: shake2ReportApi.initAndGet(applicationContext as Application)

        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.reportIdArea.setOnClickListener { _ ->
            copyToClipboard(binding.reportId.text)
            toast(R.string.shake2report_copy_sentry_copy_toast)
        }

        Shake2ReportDialog.show(this, onCancel = {
            finish()
        }, onSuccess = { message ->
                startReportError(message)
            })
    }

    private fun startReportError(message: String) {
        lifecycleScope.launch {
            reportStatus(R.string.shake2report_activity_status_screenshot)
            saveScreenshotToLogFolder()
            reportStatus(R.string.shake2report_activity_status_zipping)
            val file = compressLogFolder()
            reportStatus(R.string.shake2report_activity_status_sending)
            val id = sendingReport(message, file)
            finishInternal(id)
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
        /*val screenshot = Shake2ReportApiImpl.instance?.getScreenshotAndReset() ?: return@withContext null
        val screenshotFile = File(logDir, FILE_SCREENSHOT)
        screenshotFile.delete()
        screenshotFile.outputStream().use { out ->
            screenshot.compress(Bitmap.CompressFormat.PNG, QUALITY_SCREENSHOT, out)
        }*/
    }

    private suspend fun sendingReport(
        userInput: String,
        logZip: File
    ): String = withContext(Dispatchers.IO) {
        val event = SentryEvent()
        event.level = SentryLevel.ERROR
        event.message = Message().apply {
            message = if (userInput.isBlank()) {
                "Error via shake2report ${System.currentTimeMillis()}"
            } else userInput
        }

        lateinit var sentryId: SentryId
        Sentry.withScope { scope ->
            scope.addAttachment(Attachment(logZip.absolutePath))
            sentryId = Sentry.captureEvent(event)
            Sentry.flush(SENTRY_TIMEOUT_MS)
            scope.clearAttachments()
        }
        return@withContext sentryId.toString()
    }

    private suspend fun reportStatus(@StringRes stringResId: Int) = withContext(Dispatchers.Main) {
        binding.status.setText(stringResId)
    }

    private suspend fun finishInternal(id: String) = withContext(Dispatchers.Main) {
        binding.progressBlock.visibility = View.GONE
        binding.reportBlock.visibility = View.VISIBLE
        binding.closeBtn.visibility = View.VISIBLE
        binding.reportId.text = id
    }

    private fun copyToClipboard(id: CharSequence) {
        val clipboardManager = ContextCompat
            .getSystemService(this, ClipboardManager::class.java)
        val clipData = ClipData.newHtmlText(
            getString(R.string.shake2report_copy_sentry_title),
            id,
            "<code>$id</code>"
        )
        clipboardManager?.setPrimaryClip(clipData)
    }
}
