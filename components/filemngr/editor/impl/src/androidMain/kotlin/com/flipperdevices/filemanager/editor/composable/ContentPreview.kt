package com.flipperdevices.filemanager.editor.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.filemanager.editor.composable.content.EditorLoadingContent
import com.flipperdevices.filemanager.editor.composable.content.ErrorContent
import com.flipperdevices.filemanager.editor.composable.content.TooBigContent

@Preview
@Composable
private fun ErrorContentPreview() {
    FlipperThemeInternal {
        Scaffold {
            ErrorContent(modifier = Modifier.padding(it))
        }
    }
}

@Preview
@Composable
private fun LoadingContentPreview() {
    FlipperThemeInternal {
        Scaffold {
            EditorLoadingContent(modifier = Modifier.padding(it))
        }
    }
}

@Preview
@Composable
private fun TooBigContentPreview() {
    FlipperThemeInternal {
        Scaffold {
            TooBigContent(modifier = Modifier.padding(it))
        }
    }
}
