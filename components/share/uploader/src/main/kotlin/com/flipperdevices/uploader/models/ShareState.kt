package com.flipperdevices.uploader.models

sealed class UploaderState {
    object Initial : UploaderState()
    object Completed : UploaderState()
    object CreatingLink : UploaderState()
    data class PendingShare(val content: ShareContent) : UploaderState()

    data class Error(val typeError: ShareContentError) : UploaderState()
}
