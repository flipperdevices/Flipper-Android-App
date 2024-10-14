package com.flipperdevices.filemanager.ui.components.itemcard.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItem
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.iconResource
import com.flipperdevices.core.ui.theme.LocalPalletV2
import flipperapp.components.filemngr.ui_components.generated.resources.ic_file_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic_file_white
import flipperapp.components.filemngr.ui_components.generated.resources.ic_folder_black
import flipperapp.components.filemngr.ui_components.generated.resources.ic_folder_white
import org.jetbrains.compose.resources.painterResource
import java.io.File
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun ListingItem.asPainter(): Painter {
    val keyType = FlipperKeyType.getByExtension(File(fileName).extension)
    return when {
        fileType == FileType.DIR -> painterResource(
            when {
                MaterialTheme.colors.isLight -> FR.drawable.ic_folder_black
                else -> FR.drawable.ic_folder_white
            }
        )

        keyType != null -> painterResource(keyType.iconResource)

        else -> painterResource(
            when {
                MaterialTheme.colors.isLight -> FR.drawable.ic_file_black
                else -> FR.drawable.ic_file_white
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
