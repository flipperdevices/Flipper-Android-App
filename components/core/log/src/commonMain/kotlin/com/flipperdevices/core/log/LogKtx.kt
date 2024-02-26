package com.flipperdevices.core.log

/**
 * Main idea of this class - remove debug log execution in prod
 */
expect inline fun error(logMessage: () -> String)

expect inline fun error(error: Throwable, logMessage: () -> String)

expect inline fun info(logMessage: () -> String)

expect inline fun verbose(logMessage: () -> String)

expect inline fun warn(logMessage: () -> String)

expect inline fun debug(logMessage: () -> String)

expect inline fun wtf(logMessage: () -> String)
