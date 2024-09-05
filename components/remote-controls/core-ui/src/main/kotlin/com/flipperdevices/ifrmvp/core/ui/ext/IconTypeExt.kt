package com.flipperdevices.ifrmvp.core.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.remotecontrols.core.ui.R

@Suppress("CyclomaticComplexMethod")
@Composable
fun IconButtonData.IconType.asPainter() = when (this) {
    IconButtonData.IconType.BACK -> R.drawable.ic_back
    IconButtonData.IconType.HOME -> R.drawable.ic_home
    IconButtonData.IconType.INFO -> R.drawable.ic_info
    IconButtonData.IconType.MORE -> R.drawable.ic_more_points
    IconButtonData.IconType.MUTE -> R.drawable.ic_mute
    IconButtonData.IconType.POWER -> R.drawable.ic_pwr
    IconButtonData.IconType.COOL -> R.drawable.ic_cool
    IconButtonData.IconType.HEAT -> R.drawable.ic_heat_add
    IconButtonData.IconType.CAMERA -> R.drawable.ic_shutter
    IconButtonData.IconType.BRIGHT_MORE -> R.drawable.ic_brightness_up
    IconButtonData.IconType.BRIGHT_LESS -> R.drawable.ic_brightness_down
    IconButtonData.IconType.PAUSE -> R.drawable.ic_pause
    IconButtonData.IconType.PLAY -> R.drawable.ic_play
    IconButtonData.IconType.STOP -> R.drawable.ic_stop
    IconButtonData.IconType.ENERGY_SAVE -> R.drawable.ic_energy_save_eco
    IconButtonData.IconType.EXIT -> R.drawable.ic_exit
    IconButtonData.IconType.MENU -> R.drawable.ic_menu
    IconButtonData.IconType.ZOOM_IN -> R.drawable.ic_zoom_up
    IconButtonData.IconType.ZOOM_OUT -> R.drawable.ic_zoom_down
    IconButtonData.IconType.RESET -> R.drawable.ic_reset
    IconButtonData.IconType.NEXT -> R.drawable.ic_next
    IconButtonData.IconType.PREVIOUS -> R.drawable.ic_previous
    IconButtonData.IconType.EJECT -> R.drawable.ic_eject
    IconButtonData.IconType.RECORD -> R.drawable.ic_record
    IconButtonData.IconType.WIND_SPEED -> R.drawable.ic_wind_speed
    IconButtonData.IconType.MODE -> R.drawable.ic_mode
    IconButtonData.IconType.LIGHT -> R.drawable.ic_light
    IconButtonData.IconType.TIMER -> R.drawable.ic_timer
    IconButtonData.IconType.DELETE -> R.drawable.ic_delete
    IconButtonData.IconType.LIVE_TV -> R.drawable.ic_live_tv
    IconButtonData.IconType.FAVORITE -> R.drawable.ic_favorite
    IconButtonData.IconType.VOL_UP -> R.drawable.ic_vol_up
    IconButtonData.IconType.VOL_DOWN -> R.drawable.ic_vol_down
    IconButtonData.IconType.ADD_PLUS_MORE -> R.drawable.ic_add_plus_more
    IconButtonData.IconType.AUX -> R.drawable.ic_aux
    IconButtonData.IconType.CH_DOWN -> R.drawable.ic_ch_down
    IconButtonData.IconType.CH_UP -> R.drawable.ic_ch_up
    IconButtonData.IconType.COLD_WIND -> R.drawable.ic_cold_wind
    IconButtonData.IconType.DOWN -> R.drawable.ic_down
    IconButtonData.IconType.FAN_HIGH -> R.drawable.ic_fan_high
    IconButtonData.IconType.FAN_MEDIUM -> R.drawable.ic_fan_medium
    IconButtonData.IconType.FAN_LOW -> R.drawable.ic_fan_low
    IconButtonData.IconType.FAN_OFF -> R.drawable.ic_fan_off
    IconButtonData.IconType.FAN_SPEED -> R.drawable.ic_fan_speed
    IconButtonData.IconType.FAN_SPEED_DOWN -> R.drawable.ic_fan_speed_down
    IconButtonData.IconType.FAN_SPEED_UP -> R.drawable.ic_fan_speed_up
    IconButtonData.IconType.FAR -> R.drawable.ic_far
    IconButtonData.IconType.FOCUS_LESS -> R.drawable.ic_focus_less
    IconButtonData.IconType.FOCUS_MORE -> R.drawable.ic_focus_more
    IconButtonData.IconType.FORWARD -> R.drawable.ic_forw_forward
    IconButtonData.IconType.HEAT_ADD -> R.drawable.ic_heat_add
    IconButtonData.IconType.HEAT_REDUCE -> R.drawable.ic_heat_reduce
    IconButtonData.IconType.LEFT -> R.drawable.ic_left
    IconButtonData.IconType.NEAR -> R.drawable.ic_near
    IconButtonData.IconType.OK -> R.drawable.ic_ok
    IconButtonData.IconType.OSCILLATE -> R.drawable.ic_oscillate
    IconButtonData.IconType.REMOVE_MINUS_LESS -> R.drawable.ic_remove_minus_less
    IconButtonData.IconType.REWIND -> R.drawable.ic_rew_rewind
    IconButtonData.IconType.RIGHT -> R.drawable.ic_right
    IconButtonData.IconType.SETTINGS -> R.drawable.ic_set_settings
    IconButtonData.IconType.SHAKE_WIND -> R.drawable.ic_shake_wind
    IconButtonData.IconType.SLEEP -> R.drawable.ic_sleep
    IconButtonData.IconType.SWING -> R.drawable.ic_swing
    IconButtonData.IconType.TEMPERATURE_DOWN -> R.drawable.ic_temperature_down
    IconButtonData.IconType.TEMPERATURE_UP -> R.drawable.ic_temperature_up
    IconButtonData.IconType.TIMER_ADD -> R.drawable.ic_timer_add
    IconButtonData.IconType.TIMER_REDUCE -> R.drawable.ic_timer_reduce
    IconButtonData.IconType.TV -> R.drawable.ic_tv
    IconButtonData.IconType.UP -> R.drawable.ic_up
    IconButtonData.IconType.VOD -> R.drawable.ic_vod
    IconButtonData.IconType.WIND_TYPE -> R.drawable.ic_wind_type
}.let { painterResource(it) }
