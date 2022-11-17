plugins {
    id("flipper.lint")
    id("flipper.android-lib")
}

android {
    defaultConfig {
        val applicationId = com.flipperdevices.buildlogic.ApkConfig.APPLICATION_ID
        val shareFileAuthorities = "$applicationId.filemanager.export.provider"
        manifestPlaceholders["shareFileAuthorities"] = shareFileAuthorities
        buildConfigField("String", "SHARE_FILE_AUTHORITIES", "\"$shareFileAuthorities\"")
    }
}

dependencies {
    implementation(projects.components.core.ktx)
    implementation(libs.annotations)
    implementation(libs.appcompat)
}
