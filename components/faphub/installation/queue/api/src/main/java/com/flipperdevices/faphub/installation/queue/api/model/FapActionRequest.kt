package com.flipperdevices.faphub.installation.queue.api.model

import com.flipperdevices.faphub.dao.api.model.FapItemVersion
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

sealed class FapActionRequest {
    abstract val applicationUid: String

    data class Install(
        val applicationAlias: String,
        override val applicationUid: String,
        val applicationName: String,
        val toVersion: FapItemVersion,
        val categoryAlias: String,
        val iconUrl: String
    ) : FapActionRequest()

    data class Update(
        val from: FapManifestItem,
        val toVersion: FapItemVersion,
        val iconUrl: String,
        val applicationName: String
    ) : FapActionRequest() {
        override val applicationUid = from.uid
    }

    data class Delete(
        override val applicationUid: String
    ) : FapActionRequest()

    data class Cancel(
        override val applicationUid: String
    ) : FapActionRequest()
}
