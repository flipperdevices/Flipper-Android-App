package com.flipperdevices.archive.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.archive.impl.composable.page.GeneralPage

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableArchive() {
    GeneralPage(onCategoryPress = {}, onDeletedPress = {})
}
