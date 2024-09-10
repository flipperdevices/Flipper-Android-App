package com.flipperdevices.ifrmvp.core.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.remotecontrols.core.ui.R as RemoteControlsR

@Suppress("CyclomaticComplexMethod", "LongMethod")
@Composable
fun IconButtonData.IconType.asPainter() = when (this) {
    IconButtonData.IconType.BACK -> RemoteControlsR.drawable.ic_rc_back
    IconButtonData.IconType.HOME -> RemoteControlsR.drawable.ic_rc_home
    IconButtonData.IconType.INFO -> RemoteControlsR.drawable.ic_rc_info
    IconButtonData.IconType.MORE -> RemoteControlsR.drawable.ic_rc_more_points
    IconButtonData.IconType.MUTE -> RemoteControlsR.drawable.ic_rc_mute
    IconButtonData.IconType.POWER -> RemoteControlsR.drawable.ic_rc_pwr
    IconButtonData.IconType.COOL -> RemoteControlsR.drawable.ic_rc_cool
    IconButtonData.IconType.HEAT -> RemoteControlsR.drawable.ic_rc_heat_add
    IconButtonData.IconType.CAMERA -> RemoteControlsR.drawable.ic_rc_shutter
    IconButtonData.IconType.BRIGHT_MORE -> RemoteControlsR.drawable.ic_rc_brightness_up
    IconButtonData.IconType.BRIGHT_LESS -> RemoteControlsR.drawable.ic_rc_brightness_down
    IconButtonData.IconType.PAUSE -> RemoteControlsR.drawable.ic_rc_pause
    IconButtonData.IconType.PLAY -> RemoteControlsR.drawable.ic_rc_play
    IconButtonData.IconType.STOP -> RemoteControlsR.drawable.ic_rc_stop
    IconButtonData.IconType.ENERGY_SAVE -> RemoteControlsR.drawable.ic_rc_energy_save_eco
    IconButtonData.IconType.EXIT -> RemoteControlsR.drawable.ic_rc_exit
    IconButtonData.IconType.MENU -> RemoteControlsR.drawable.ic_rc_menu
    IconButtonData.IconType.ZOOM_IN -> RemoteControlsR.drawable.ic_rc_zoom_up
    IconButtonData.IconType.ZOOM_OUT -> RemoteControlsR.drawable.ic_rc_zoom_down
    IconButtonData.IconType.RESET -> RemoteControlsR.drawable.ic_rc_reset
    IconButtonData.IconType.NEXT -> RemoteControlsR.drawable.ic_rc_next
    IconButtonData.IconType.PREVIOUS -> RemoteControlsR.drawable.ic_rc_previous
    IconButtonData.IconType.EJECT -> RemoteControlsR.drawable.ic_rc_eject
    IconButtonData.IconType.RECORD -> RemoteControlsR.drawable.ic_rc_record
    IconButtonData.IconType.WIND_SPEED -> RemoteControlsR.drawable.ic_rc_wind_speed
    IconButtonData.IconType.MODE -> RemoteControlsR.drawable.ic_rc_mode
    IconButtonData.IconType.LIGHT -> RemoteControlsR.drawable.ic_rc_light
    IconButtonData.IconType.TIMER -> RemoteControlsR.drawable.ic_rc_timer
    IconButtonData.IconType.DELETE -> RemoteControlsR.drawable.ic_rc_delete
    IconButtonData.IconType.LIVE_TV -> RemoteControlsR.drawable.ic_rc_live_tv
    IconButtonData.IconType.FAVORITE -> RemoteControlsR.drawable.ic_rc_favorite
    IconButtonData.IconType.VOL_UP -> RemoteControlsR.drawable.ic_rc_vol_up
    IconButtonData.IconType.VOL_DOWN -> RemoteControlsR.drawable.ic_rc_vol_down
    IconButtonData.IconType.ADD_PLUS_MORE -> RemoteControlsR.drawable.ic_rc_add_plus_more
    IconButtonData.IconType.AUX -> RemoteControlsR.drawable.ic_rc_aux
    IconButtonData.IconType.CH_DOWN -> RemoteControlsR.drawable.ic_rc_ch_down
    IconButtonData.IconType.CH_UP -> RemoteControlsR.drawable.ic_rc_ch_up
    IconButtonData.IconType.COLD_WIND -> RemoteControlsR.drawable.ic_rc_cold_wind
    IconButtonData.IconType.DOWN -> RemoteControlsR.drawable.ic_rc_down
    IconButtonData.IconType.FAN_HIGH -> RemoteControlsR.drawable.ic_rc_fan_high
    IconButtonData.IconType.FAN_MEDIUM -> RemoteControlsR.drawable.ic_rc_fan_medium
    IconButtonData.IconType.FAN_LOW -> RemoteControlsR.drawable.ic_rc_fan_low
    IconButtonData.IconType.FAN_OFF -> RemoteControlsR.drawable.ic_rc_fan_off
    IconButtonData.IconType.FAN_SPEED -> RemoteControlsR.drawable.ic_rc_fan_speed
    IconButtonData.IconType.FAN_SPEED_DOWN -> RemoteControlsR.drawable.ic_rc_fan_speed_down
    IconButtonData.IconType.FAN_SPEED_UP -> RemoteControlsR.drawable.ic_rc_fan_speed_up
    IconButtonData.IconType.FAR -> RemoteControlsR.drawable.ic_rc_far
    IconButtonData.IconType.FOCUS_LESS -> RemoteControlsR.drawable.ic_rc_focus_less
    IconButtonData.IconType.FOCUS_MORE -> RemoteControlsR.drawable.ic_rc_focus_more
    IconButtonData.IconType.FORWARD -> RemoteControlsR.drawable.ic_rc_forw_forward
    IconButtonData.IconType.HEAT_ADD -> RemoteControlsR.drawable.ic_rc_heat_add
    IconButtonData.IconType.HEAT_REDUCE -> RemoteControlsR.drawable.ic_rc_heat_reduce
    IconButtonData.IconType.LEFT -> RemoteControlsR.drawable.ic_rc_left
    IconButtonData.IconType.NEAR -> RemoteControlsR.drawable.ic_rc_near
    IconButtonData.IconType.OK -> RemoteControlsR.drawable.ic_rc_ok
    IconButtonData.IconType.OSCILLATE -> RemoteControlsR.drawable.ic_rc_oscillate
    IconButtonData.IconType.REMOVE_MINUS_LESS -> RemoteControlsR.drawable.ic_rc_remove_minus_less
    IconButtonData.IconType.REWIND -> RemoteControlsR.drawable.ic_rc_rew_rewind
    IconButtonData.IconType.RIGHT -> RemoteControlsR.drawable.ic_rc_right
    IconButtonData.IconType.SETTINGS -> RemoteControlsR.drawable.ic_rc_set_settings
    IconButtonData.IconType.SHAKE_WIND -> RemoteControlsR.drawable.ic_rc_shake_wind
    IconButtonData.IconType.SLEEP -> RemoteControlsR.drawable.ic_rc_sleep
    IconButtonData.IconType.SWING -> RemoteControlsR.drawable.ic_rc_swing
    IconButtonData.IconType.TEMPERATURE_DOWN -> RemoteControlsR.drawable.ic_rc_temperature_down
    IconButtonData.IconType.TEMPERATURE_UP -> RemoteControlsR.drawable.ic_rc_temperature_up
    IconButtonData.IconType.TIMER_ADD -> RemoteControlsR.drawable.ic_rc_timer_add
    IconButtonData.IconType.TIMER_REDUCE -> RemoteControlsR.drawable.ic_rc_timer_reduce
    IconButtonData.IconType.TV -> RemoteControlsR.drawable.ic_rc_tv
    IconButtonData.IconType.UP -> RemoteControlsR.drawable.ic_rc_up
    IconButtonData.IconType.VOD -> RemoteControlsR.drawable.ic_rc_vod
    IconButtonData.IconType.WIND_TYPE -> RemoteControlsR.drawable.ic_rc_wind_type
}.let { painterResource(it) }
