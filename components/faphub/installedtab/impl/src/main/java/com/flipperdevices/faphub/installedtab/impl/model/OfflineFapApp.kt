package com.flipperdevices.faphub.installedtab.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem


@Stable
internal abstract class InstalledFapApp {
    abstract val name: String
    abstract val iconBase64: String?
    abstract val category: String
    abstract val applicationUid: String
    abstract val applicationAlias: String
}

@Stable
internal data class OfflineFapApp(
    override val name: String,
    override val iconBase64: String?,
    override val category: String,
    override val applicationUid: String,
    override val applicationAlias: String
): InstalledFapApp() {
    constructor(fapManifestItem: FapManifestItem) : this(
        name = fapManifestItem.fullName,
        iconBase64 = fapManifestItem.iconBase64,
        category = extractCategoryFromPath(fapManifestItem.path),
        applicationUid = fapManifestItem.uid,
        applicationAlias = fapManifestItem.applicationAlias
    )
}
@Stable
internal data class OnlineFapApp(
    override val name: String,
    override val iconBase64: String?,
    override val category: String,
    override val applicationUid: String,
    override val applicationAlias: String
) : InstalledFapApp()

private const val UNKNOWN_CATEGORY = "Unknown"

private fun extractCategoryFromPath(path: String): String {
    val paths = path.split("/")
    // Example path: /ext/apps/category/app_name.fap
    return paths.getOrElse(paths.size - 2) { UNKNOWN_CATEGORY }
}
