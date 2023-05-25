package com.flipperdevices.faphub.installation.queue.impl.model

sealed class FapActionRequest {
    data class Install(
        val applicationId: String,
        val applicationUid: String,
        val toVersionId: String
    ) : FapActionRequest()
}
