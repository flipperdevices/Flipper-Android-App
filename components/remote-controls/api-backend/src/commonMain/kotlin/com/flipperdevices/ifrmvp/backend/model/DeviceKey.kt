package com.flipperdevices.ifrmvp.backend.model

/**
 * [DeviceKey] is default set of pre-defined keys for remote controls
 *
 * The keys have comments in which devices they are present
 */
enum class DeviceKey {
    // TV, Air_Purifiers, Box, DVD, Fan, Projector
    PWR,

    // TV, Box, Camera, DVD, Projector
    VOL_UP,

    // TV, Box, Camera, DVD, Projector
    VOL_DOWN,

    // TV, Box
    CH_UP,

    // TV, Box
    CH_DOWN,

    // Projector
    FOCUS_MORE,

    // Projector
    FOCUS_LESS,

    // Projector
    ZOOM_UP,

    // Projector
    ZOOM_DOWN,

    // Projector
    RESET,

    // A_V_receiver, Box, Camera, DVD, Projector
    DOWN,

    // A_V_receiver, Box, Camera, DVD, Projector
    UP,

    // A_V_receiver, Box, Camera, DVD, Projector
    RIGHT,

    // A_V_receiver, Box, Camera, DVD, Projector
    LEFT,

    // A_V_receiver, Camera, DVD
    NEXT,

    // A_V_receiver, Camera, DVD
    PREVIOUS,

    // A_V_receiver
    TV,

    // A_V_receiver
    AUX,

    // A_V_receiver, Box, DVD
    HOME,

    // A_V_receiver, Box, Camera, Projector
    BACK,

    // A_V_receiver, Camera, DVD, Projector
    MENU,

    // A_V_receiver, DVD
    PLAY,

    // A_V_receiver, Air_Purifiers, DVD, Fan, Projector
    MUTE,

    // Air_Purifiers
    FAN_SPEED,

    // Camera
    NEAR,

    // Camera
    FAR,

    // DVD
    PAUSE,

    // Fan
    WIND_SPEED,

    // Fan
    MODE,

    // Fan
    FAN_SPEED_UP,

    // Fan
    FAN_SPEED_DOWN,

    // Fan
    SHAKE_WIND,

    // Fan
    WIND_TYPE,

    // Fan
    TEMPERATURE_UP,

    // Fan
    TEMPERATURE_DOWN,

    // Fan
    ENERGY_SAVE
}
