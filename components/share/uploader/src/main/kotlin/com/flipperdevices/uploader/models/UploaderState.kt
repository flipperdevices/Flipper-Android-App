package com.flipperdevices.uploader.models

sealed class UploaderState {
    object Chooser : UploaderState()
    object Completed : UploaderState()
    data class Prepare(val isLongKey: Boolean) : UploaderState()
    data class Error(val typeError: ShareContentError) : UploaderState()
}
