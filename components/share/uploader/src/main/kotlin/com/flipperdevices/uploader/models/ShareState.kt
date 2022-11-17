package com.flipperdevices.uploader.models

import com.flipperdevices.share.api.ShareContentError

sealed class ShareState {
    object Initial : ShareState()
    object Completed : ShareState()
    object Prepare : ShareState()
    data class PendingShare(val content: ShareContent) : ShareState()

    data class Error(val typeError: ShareContentError) : ShareState()
}
