package com.flipperdevices.filemanager.ui.components.name

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import kotlinx.collections.immutable.persistentListOf
import java.io.File

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun NameDialogPreview() {
    var isVisible by remember { mutableStateOf(true) }
    var value by remember { mutableStateOf("text") }
    val options = persistentListOf(
        ".txt",
        ".ir",
        ".nfc"
    )
    FlipperThemeInternal {
        Scaffold {
            ComposableFlipperButton("Show", onClick = { isVisible = !isVisible })
            if (isVisible) {
                NameDialog(
                    value = value,
                    title = "Enter Name:",
                    buttonText = "Save",
                    subtitle = "Allowed characters: “0-9”, “A-Z”, “a-z”, “!#\\\$%&'()-@^_`{}~”",
                    onTextChange = { value = it },
                    onOptionSelect = onOptionSelected@{ index ->
                        val extensionWithDot = options.getOrNull(index) ?: return@onOptionSelected
                        value = "$value$extensionWithDot"
                    },
                    options = options,
                    onDismissRequest = { isVisible = !isVisible },
                    needShowOptions = File(value).extension.isBlank(),
                    isError = File(value).extension.isBlank(),
                    onFinish = { },
                    isEnabled = true,
                    isLoading = false
                )
            }
        }
    }
}
