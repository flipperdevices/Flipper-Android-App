package com.flipperdevices.core.log

@Suppress("PropertyName")
interface LogTagProvider {
    @Suppress("VariableNaming")
    val TAG: String
    class Default(override val TAG: String) : LogTagProvider
}

expect inline fun LogTagProvider.error(logMessage: () -> String)

expect inline fun LogTagProvider.error(error: Throwable?, logMessage: () -> String)

expect inline fun LogTagProvider.info(logMessage: () -> String)

expect inline fun LogTagProvider.verbose(logMessage: () -> String)

expect inline fun LogTagProvider.warn(logMessage: () -> String)

expect inline fun LogTagProvider.debug(logMessage: () -> String)

expect inline fun LogTagProvider.wtf(logMessage: () -> String)
