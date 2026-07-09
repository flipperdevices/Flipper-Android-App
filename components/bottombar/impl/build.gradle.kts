plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.bottombar.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.info.api)
    implementation(projects.components.connection.api)
    implementation(projects.components.archive.api)
    implementation(projects.components.inappnotification.api)
    implementation(projects.components.toolstab.api)
    implementation(projects.components.faphub.main.api)
    implementation(projects.components.faphub.installedtab.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.unhandledexception.api)
    implementation(projects.components.notification.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.appcompat)

    implementation(libs.compose.pager)
    implementation(libs.bundles.decompose)
    implementation(libs.image.lottie)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.lifecycle.runtime.ktx)
}
