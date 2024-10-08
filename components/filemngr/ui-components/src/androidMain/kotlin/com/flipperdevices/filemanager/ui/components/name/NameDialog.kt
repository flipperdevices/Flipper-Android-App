package com.flipperdevices.filemanager.ui.components.name

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.io.File

@Composable
fun NameDialog(
    value: String,
    title: String,
    buttonText: String,
    subtitle: String,
    needShowOptions: Boolean,
    isError: Boolean,
    isEnabled: Boolean,
    onTextChange: (String) -> Unit,
    options: ImmutableList<String>,
    onOptionSelect: (index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    onFinish: () -> Unit
) {
    DisableSelection {
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalPallet.current.backgroundDialog)
                    .padding(17.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Close),
                        tint = LocalPalletV2.current.icon.blackAndWhite.default,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismissRequest)
                    )
                }
                Spacer(Modifier.height(24.dp))
                AutoCompleteTextField(
                    value = value,
                    onTextChange = onTextChange,
                    options = options,
                    onOptionSelect = onOptionSelect,
                    needShowOptions = needShowOptions,
                    title = title,
                    subtitle = subtitle,
                    isError = isError
                )

                Spacer(Modifier.height(24.dp))
                ComposableFlipperButton(
                    text = buttonText,
                    modifier = Modifier.fillMaxWidth(),
                    textPadding = PaddingValues(vertical = 12.dp),
                    onClick = onFinish,
                    enabled = isEnabled
                )
            }
        }
    }
}

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
                    isEnabled = true
                )
            }
        }
    }
}
