package com.flipperdevices.filemanager.ui.components.name

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.ktx.elements.ComposableFlipperButton
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NameDialog(
    value: String,
    title: String,
    buttonText: String,
    subtitle: String,
    needShowOptions: Boolean,
    isError: Boolean,
    isEnabled: Boolean,
    isLoading: Boolean,
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
                    isError = isError,
                    isEnabled = isEnabled
                )

                Spacer(Modifier.height(24.dp))
                ComposableFlipperButton(
                    text = buttonText,
                    modifier = Modifier.fillMaxWidth(),
                    textPadding = PaddingValues(vertical = 12.dp),
                    onClick = onFinish,
                    enabled = isEnabled,
                    isLoading = isLoading
                )
            }
        }
    }
}
