package com.flipperdevices.uploader.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.uploader.models.ShareContentError
import com.flipperdevices.uploader.models.UploaderState
import java.io.File

private val flipperKey = FlipperKey(
    mainFile = FlipperFile(
        path = FlipperFilePath(
            folder = "test",
            nameWithExtension = "test.sub"
        ),
        content = FlipperKeyContent.InternalFile(file = File(""))
    ),
    synchronized = false,
    deleted = false
)

@Preview
@Composable
private fun ComposableSheetContentPrepareLongLinkPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Prepare(isLongKey = true),
            flipperKey = flipperKey
        )
    }
}

@Preview
@Composable
private fun ComposableSheetContentPrepareShortLinkPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Prepare(isLongKey = false),
            flipperKey = flipperKey
        )
    }
}

@Preview
@Composable
private fun ComposableSheetContentChooserLinkPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Chooser,
            flipperKey = flipperKey
        )
    }
}

@Preview
@Composable
private fun ComposableSheetContentErrorInternetPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Error(ShareContentError.NO_INTERNET),
            flipperKey = flipperKey
        )
    }
}

@Preview
@Composable
private fun ComposableSheetContentErrorServerPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Error(ShareContentError.SERVER_ERROR),
            flipperKey = flipperKey
        )
    }
}

@Preview
@Composable
private fun ComposableSheetContentErrorOtherPreview() {
    FlipperThemeInternal {
        ComposableSheetContent(
            state = UploaderState.Error(ShareContentError.OTHER),
            flipperKey = flipperKey
        )
    }
}
