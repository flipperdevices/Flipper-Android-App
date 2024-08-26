package com.flipperdevices.ifrmvp.core.ui.ext

import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SettingsPower
import androidx.compose.material.icons.filled.SevereCold
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData

@Composable
fun IconButtonData.IconType.asPainter() = when (this) {
    IconButtonData.IconType.BACK -> rememberVectorPainter(Icons.AutoMirrored.Filled.Reply)
    IconButtonData.IconType.HOME -> rememberVectorPainter(Icons.Outlined.Home)
    IconButtonData.IconType.INFO -> rememberVectorPainter(Icons.Outlined.Info)
    IconButtonData.IconType.MORE -> rememberVectorPainter(Icons.Filled.MoreHoriz)
    IconButtonData.IconType.MUTE -> rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeOff)
    IconButtonData.IconType.POWER -> rememberVectorPainter(Icons.Default.SettingsPower)
    IconButtonData.IconType.COOL -> rememberVectorPainter(Icons.Default.SevereCold)
    IconButtonData.IconType.HEAT -> rememberVectorPainter(Icons.Default.Fireplace)
    IconButtonData.IconType.FAN -> rememberVectorPainter(Icons.Default.Air)
    IconButtonData.IconType.CAMERA -> rememberVectorPainter(Icons.Default.CameraAlt)
}
