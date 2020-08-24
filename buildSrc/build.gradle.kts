plugins {
  id("org.jetbrains.kotlin.jvm") version "1.4.0"
}

repositories {
  mavenCentral()
  google()
  jcenter()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
  implementation("com.android.tools.build:gradle:4.0.1")
}
