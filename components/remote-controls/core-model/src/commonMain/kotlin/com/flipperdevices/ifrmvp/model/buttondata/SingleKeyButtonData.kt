package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.Serializable

@Serializable
sealed interface SingleKeyButtonData : ButtonData {
    val keyIdentifier: IfrKeyIdentifier
}
