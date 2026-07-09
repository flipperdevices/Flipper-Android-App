plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.share.receive"

androidDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.share.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.bottombar.api)
    implementation(projects.components.archive.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.inappnotification.api)

    implementation(libs.appcompat)

    implementation(libs.ktor.serialization)
    implementation(libs.ktor.client)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.lifecycle.compose)

    // Compose
    implementation(libs.bundles.decompose)

    // Testing
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockk)
    "androidUnitTestImplementation"(libs.mockito.kotlin)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.lifecycle.test)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
}
