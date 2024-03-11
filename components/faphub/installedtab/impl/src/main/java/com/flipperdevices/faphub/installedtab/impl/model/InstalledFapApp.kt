package com.flipperdevices.faphub.installedtab.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem

@Stable
sealed class InstalledFapApp {
    abstract val name: String
    abstract val applicationUid: String
    abstract val applicationAlias: String

    @Stable
    data class OfflineFapApp(
        override val name: String,
        override val applicationUid: String,
        override val applicationAlias: String,
        val iconBase64: String?,
        val category: String
    ) : InstalledFapApp() {
        constructor(fapManifestItem: FapManifestItem) : this(
            name = fapManifestItem.fullName,
            iconBase64 = fapManifestItem.iconBase64,
            category = extractCategoryFromPath(fapManifestItem.path),
            applicationUid = fapManifestItem.uid,
            applicationAlias = fapManifestItem.applicationAlias
        )
    }

    @Stable
    data class OnlineFapApp(
        val fapItemShort: FapItemShort
    ) : InstalledFapApp() {
        override val name = fapItemShort.name
        override val applicationUid = fapItemShort.id
        override val applicationAlias = fapItemShort.applicationAlias
    }
}

private const val UNKNOWN_CATEGORY = "Unknown"

private fun extractCategoryFromPath(path: String): String {
    val paths = path.split("/")
    // Example path: /ext/apps/category/app_name.fap
    return paths.getOrElse(paths.size - 2) { UNKNOWN_CATEGORY }
}
