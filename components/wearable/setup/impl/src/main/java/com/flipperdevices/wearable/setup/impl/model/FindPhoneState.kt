package com.flipperdevices.wearable.setup.impl.model

sealed class FindPhoneState {
    data object Loading : FindPhoneState()
    data object NotFound : FindPhoneState()
    data object Founded : FindPhoneState()
}
