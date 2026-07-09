plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.faphub.uninstallbutton.api"

androidDependencies {

    implementation(projects.components.faphub.dao.api)
}
