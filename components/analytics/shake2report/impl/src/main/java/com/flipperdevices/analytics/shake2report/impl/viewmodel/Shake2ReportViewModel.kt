package com.flipperdevices.analytics.shake2report.impl.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat
import com.flipperdevices.analytics.shake2report.impl.InternalShake2Report
import com.flipperdevices.analytics.shake2report.impl.R
import com.flipperdevices.analytics.shake2report.impl.model.Shake2ReportState
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import io.sentry.Attachment
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import io.sentry.protocol.SentryId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("MagicNumber")
private val SENTRY_TIMEOUT_MS = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)

class Shake2ReportViewModel @Inject constructor(
    private val application: Application,
    private val internalShake2Report: InternalShake2Report
) : DecomposeViewModel() {
    private val shake2ReportStateFlow = MutableStateFlow<Shake2ReportState>(
        Shake2ReportState.Pending
    )

    fun getState() = shake2ReportStateFlow.asStateFlow()

    fun report(
        name: String,
        description: String,
        addLogs: Boolean
    ) = viewModelScope.launch {
        if (shake2ReportStateFlow.value !is Shake2ReportState.Pending) {
            return@launch
        }

        runCatching {
            shake2ReportStateFlow.emit(Shake2ReportState.Uploading)
            val compressingFile = if (addLogs) compressLogFolder() else null
            val sentryId = sendingReport(name, description, compressingFile)
            shake2ReportStateFlow.emit(Shake2ReportState.Complete(sentryId))
        }.onFailure {
            shake2ReportStateFlow.emit(Shake2ReportState.Error)
        }
    }

    fun copyToClipboard() {
        val state = shake2ReportStateFlow.value
        if (state !is Shake2ReportState.Complete) return
        val id = state.id

        val clipboardManager = ContextCompat.getSystemService(application, ClipboardManager::class.java)
        val clipData = ClipData.newHtmlText(
            application.getString(R.string.shake2report_copy_sentry_title),
            id,
            "<code>$id</code>"
        )
        clipboardManager?.setPrimaryClip(clipData)
        application.toast(R.string.shake2report_copy_sentry_copy_toast)
    }

    private suspend fun compressLogFolder(): File = withContext(Dispatchers.IO) {
        val logDir = internalShake2Report.logDir
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

    private suspend fun sendingReport(
        name: String,
        description: String,
        logZip: File?
    ): String = withContext(Dispatchers.IO) {
        val event = SentryEvent()
        event.level = SentryLevel.ERROR
        event.message = Message().apply {
            message = name.ifBlank {
                "Error via shake2report ${System.currentTimeMillis()}"
            }
            params = (params ?: emptyList()).plus(description.split("\n"))
        }

        lateinit var sentryId: SentryId
        Sentry.withScope { scope ->
            logZip?.let { logs ->
                scope.addAttachment(Attachment(logs.absolutePath))
            }
            sentryId = Sentry.captureEvent(event)
            Sentry.flush(SENTRY_TIMEOUT_MS)
            scope.clearAttachments()
        }
        return@withContext sentryId.toString()
    }
}
