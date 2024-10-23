package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.icon
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.filemanager.ui.components.R
import java.io.File

@Composable
fun ListingItem.asPainter(): Painter {
    val keyType = FlipperKeyType.getByExtension(File(fileName).extension)
    return when {
        fileType == FileType.DIR -> painterResource(
            when {
                MaterialTheme.colors.isLight -> R.drawable.ic_folder_black
                else -> R.drawable.ic_folder_white
            }
        )

        keyType != null -> painterResource(keyType.icon)

        else -> painterResource(
            when {
                MaterialTheme.colors.isLight -> R.drawable.ic_file_black
                else -> R.drawable.ic_file_white
            }
        )
    }
}

@Composable
fun ListingItem.asTint(): Color {
    val keyType = FlipperKeyType.getByExtension(File(fileName).extension)
    return when {
        fileType == FileType.DIR -> Color.Unspecified

        keyType != null -> LocalPalletV2.current.icon.blackAndWhite.default

        else -> Color.Unspecified
    }
}
