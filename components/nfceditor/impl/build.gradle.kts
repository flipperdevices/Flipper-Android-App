plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.nfceditor.api)

    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.hexkeyboard)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.di)
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.keyscreen.shared)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.appcompat)

    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)

    implementation(libs.lifecycle.compose)
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(projects.components.core.buildKonfig)
    implementation(libs.roboelectric)
    implementation(libs.junit)
    implementation(libs.ktx.testing)
    implementation(libs.mockito.kotlin)
    implementation(projects.components.keyparser.impl)
}
