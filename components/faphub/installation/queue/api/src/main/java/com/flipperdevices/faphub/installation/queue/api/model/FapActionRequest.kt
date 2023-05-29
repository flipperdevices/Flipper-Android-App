package com.flipperdevices.faphub.installation.queue.api.model

sealed class FapActionRequest {
    abstract val applicationUid: String

    data class Install(
        val applicationAlias: String,
        override val applicationUid: String,
        val toVersionId: String,
        val categoryAlias: String
    ) : FapActionRequest()

    data class Cancel(
        override val applicationUid: String
    ) : FapActionRequest()
}
