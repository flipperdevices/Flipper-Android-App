plugins {
    id("flipper.desktop")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

compose.desktop {
    application {
        mainClass = "com.flipperdevices.desktop.MainKt"
    }
}

dependencies {


}
