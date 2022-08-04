plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(gradleApi())
}

gradlePlugin {
    plugins.register("flipper.apk-config") {
        id = "flipper.apk-config"
        implementationClass = "com.flipperdevices.buildlogic.plugins.ApkConfigPlugin"
    }
}
