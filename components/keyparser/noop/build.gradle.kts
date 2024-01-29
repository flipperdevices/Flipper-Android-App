plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.parser.noop"

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.core.di)
}
