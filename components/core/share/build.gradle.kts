plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil-multiplatform")
}

android.namespace = "com.flipperdevices.core.share"

android {
    defaultConfig {
        val applicationId = com.flipperdevices.buildlogic.ApkConfig.APPLICATION_ID
        val shareFileAuthorities = "$applicationId.filemanager.export.provider"
        manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
        buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
    }
}

commonDependencies {
    implementation(projects.components.core.di)
    implementation(libs.okio)
}

androidDependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.annotations)
    implementation(libs.appcompat)
}
