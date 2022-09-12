package com.flipperdevices.wearable.setup.impl.model

sealed class FindPhoneState {
    object Loading : FindPhoneState()
    object NotFound : FindPhoneState()
    object Founded : FindPhoneState()
}
