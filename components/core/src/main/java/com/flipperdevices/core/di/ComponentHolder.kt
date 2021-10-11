package com.flipperdevices.core.di

/**
 * This is workaround for anvil components
 * Now components inject in AppComponent only _after_ compile, so we don't see it in IDE/sources
 * In this class we just trust that AppComponent class is instance of our component interface
 * You can see this by looking at the DaggerAppComponent class
 *
 * Copy-paste from https://github.com/square/anvil/blob/7138cd3062/sample/scopes/src/main/java/com/squareup/scopes/ComponentHolder.kt
 *
 * Yes, this is the runtime check for the compile-time framework.
 */
object ComponentHolder {
    val components = mutableSetOf<Any>()

    inline fun <reified T> component(): T = components
        .filterIsInstance<T>()
        .single()
}
