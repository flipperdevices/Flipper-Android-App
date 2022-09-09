package com.flipperdevices.wearable.setup.impl.model

sealed class FindPhoneModel {
    object Loading : FindPhoneModel()
    object NotFound : FindPhoneModel()
    class Founded(val phoneName: String) : FindPhoneModel()
}
