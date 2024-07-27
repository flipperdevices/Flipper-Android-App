plugins {
    id("flipper.android-app")
    id("flipper.anvil.entrypoint")
}

android.namespace = "com.flipperdevices.wearable"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.wearable.emulate.wear.api)
    implementation(projects.components.wearable.emulate.wear.impl)
    implementation(projects.components.wearable.emulate.common)
    implementation(projects.components.wearable.sync.wear.api)
    implementation(projects.components.wearable.sync.wear.impl)
    implementation(projects.components.wearable.core.ui.theme)
    implementation(projects.components.wearable.wearrootscreen.api)
    implementation(projects.components.wearable.wearrootscreen.impl)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyparser.noop)

    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.screenstreaming.noop)

    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyemulate.impl)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.service.noop)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.synchronization.stub)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.appcompat)
    implementation(libs.timber)
    implementation(libs.datastore)
    implementation(libs.splashscreen)

    implementation(libs.wear)
    implementation(libs.wear.gms)

    implementation(projects.components.analytics.shake2report.api)
    releaseImplementation(projects.components.analytics.shake2report.noop)
    debugImplementation(projects.components.analytics.shake2report.impl)
    internalImplementation(projects.components.analytics.shake2report.impl)

    // Compose
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.horologist.layout)
    implementation(libs.bundles.decompose)
    implementation(libs.lifecycle.compose)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines.play.services)

    // Dagger deps
    implementation(libs.dagger)
    commonKsp(libs.dagger.compiler)
}
