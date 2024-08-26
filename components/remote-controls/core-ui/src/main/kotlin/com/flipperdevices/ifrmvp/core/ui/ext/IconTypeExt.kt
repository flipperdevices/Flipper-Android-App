package com.flipperdevices.ifrmvp.core.ui.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.EmergencyRecording
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.SettingsPower
import androidx.compose.material.icons.filled.SevereCold
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData

@Suppress("CyclomaticComplexMethod")
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
    IconButtonData.IconType.BRIGHT_MORE -> rememberVectorPainter(Icons.Default.Brightness2)
    IconButtonData.IconType.BRIGHT_LESS -> rememberVectorPainter(Icons.Default.Brightness5)
    IconButtonData.IconType.PAUSE -> rememberVectorPainter(Icons.Default.Pause)
    IconButtonData.IconType.PLAY -> rememberVectorPainter(Icons.Default.PlayArrow)
    IconButtonData.IconType.STOP -> rememberVectorPainter(Icons.Default.Stop)
    IconButtonData.IconType.ENERGY_SAVE -> rememberVectorPainter(Icons.Default.EnergySavingsLeaf)
    IconButtonData.IconType.EXIT -> rememberVectorPainter(Icons.AutoMirrored.Filled.Logout)
    IconButtonData.IconType.MENU -> rememberVectorPainter(Icons.Default.Menu)
    IconButtonData.IconType.ZOOM_IN -> rememberVectorPainter(Icons.Default.ZoomIn)
    IconButtonData.IconType.ZOOM_OUT -> rememberVectorPainter(Icons.Default.ZoomOut)
    IconButtonData.IconType.RESET -> rememberVectorPainter(Icons.Default.LockReset)
    IconButtonData.IconType.NEXT -> rememberVectorPainter(Icons.Default.SkipNext)
    IconButtonData.IconType.PREVIOUS -> rememberVectorPainter(Icons.Default.SkipPrevious)
    IconButtonData.IconType.EJECT -> rememberVectorPainter(Icons.Default.Eject)
    IconButtonData.IconType.RECORD -> rememberVectorPainter(Icons.Default.EmergencyRecording)
    IconButtonData.IconType.WIND_SPEED -> rememberVectorPainter(Icons.Default.WindPower)
    IconButtonData.IconType.MODE -> rememberVectorPainter(Icons.Default.Mode)
    IconButtonData.IconType.LIGHT -> rememberVectorPainter(Icons.Default.Light)
    IconButtonData.IconType.TIMER -> rememberVectorPainter(Icons.Default.Timer)
    IconButtonData.IconType.OFF -> rememberVectorPainter(Icons.Default.PowerOff)
    IconButtonData.IconType.DELETE -> rememberVectorPainter(Icons.Default.Delete)
    IconButtonData.IconType.LIVE_TV -> rememberVectorPainter(Icons.Default.LiveTv)
    IconButtonData.IconType.FAVORITE -> rememberVectorPainter(Icons.Default.Favorite)
    IconButtonData.IconType.VOL_UP -> rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeUp)
    IconButtonData.IconType.VOL_DOWN -> rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeDown)
}
