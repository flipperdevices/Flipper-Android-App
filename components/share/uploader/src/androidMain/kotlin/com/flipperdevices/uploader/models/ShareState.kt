package com.flipperdevices.uploader.models

sealed class ShareState {
    object Initial : ShareState()
    object Completed : ShareState()
    object Prepare : ShareState()
    data class PendingShare(val content: ShareContent) : ShareState()

    data class Error(val typeError: ShareError) : ShareState()
}
