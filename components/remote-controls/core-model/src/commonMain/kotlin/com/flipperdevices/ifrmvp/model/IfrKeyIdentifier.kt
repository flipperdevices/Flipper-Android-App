package com.flipperdevices.ifrmvp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [IfrKeyIdentifier] is used to define the remote controller key
 *
 * For example, the .ir file contains multiple buttons. We need to use the ON/OFF buttons.
 * Then we define [IfrKeyIdentifier.Sha256] where [IfrKeyIdentifier.Sha256.hash]
 * is the hashcode of its raw data.
 * It's required, so we can find exact signal by its identifier
 *
 * The class should be polymorphic, so we can define different identifiers for future development.
 */
@Serializable
sealed interface IfrKeyIdentifier {
    /**
     * SHA-256 of raw data
     */
    @SerialName(Sha256.TYPE)
    @Serializable
    data class Sha256(
        @SerialName("key_name")
        val name: String,
        @SerialName("sha_256_string")
        val hash: String
    ) : IfrKeyIdentifier {
        companion object {
            const val TYPE = "SHA_256"
        }
    }

    @SerialName(Name.TYPE)
    @Serializable
    data class Name(
        @SerialName("key_name")
        val name: String,
    ) : IfrKeyIdentifier {
        companion object {
            const val TYPE = "NAME"
        }
    }

    @SerialName(Unknown.TYPE)
    @Serializable
    data object Unknown : IfrKeyIdentifier {
        const val TYPE = "EMPTY"
    }
}
