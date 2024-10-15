package com.flipperdevices.filemanager.search.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sd_card_ok_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic_sd_card_ok_white
import okio.Path
import org.jetbrains.compose.resources.painterResource
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun ListingTitleComposable(
    path: Path,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 14.dp)
            .padding(top = 4.dp)
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (path.parent != null) {
            Text(
                text = path.name,
                style = LocalTypography.current.titleSB16,
                color = LocalPalletV2.current.text.title.primary
            )
        } else {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(
                    when {
                        MaterialTheme.colors.isLight -> FR.drawable.ic_sd_card_ok_black
                        else -> FR.drawable.ic_sd_card_ok_white
                    }
                ),
                tint = Color.Unspecified,
                contentDescription = null,
            )
        }
    }
}
