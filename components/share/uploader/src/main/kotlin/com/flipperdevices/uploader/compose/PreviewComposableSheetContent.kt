package com.flipperdevices.uploader.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.uploader.models.ShareContent
import com.flipperdevices.uploader.models.ShareError
import com.flipperdevices.uploader.models.ShareState

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentErrorOther() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.Error(ShareError.OTHER),
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentErrorInternet() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.Error(ShareError.NO_INTERNET_CONNECTION),
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentErrorServer() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.Error(ShareError.CANT_CANNOT_TO_SERVER),
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentInit() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.Initial,
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentPrepare() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.Prepare,
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun PreviewComposableSheetContentPending() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = ShareState.PendingShare(
                content = ShareContent(
                    link = "https://flipperdevices.com",
                    flipperKey = FlipperKey(
                        mainFile = FlipperFile(
                            path = FlipperFilePath(
                                folder = "sub",
                                nameWithExtension = "test.sub"
                            ),
                            content = FlipperKeyContent.RawData(byteArrayOf())
                        ),
                        synchronized = true,
                        deleted = false
                    )
                )
            ),
            keyName = "keyName",
            onShareLink = {},
            onShareFile = {},
            onRetry = {},
            onClose = {}
        )
    }
}
