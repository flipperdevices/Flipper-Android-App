package com.flipperdevices.core.log

actual inline fun error(logMessage: () -> String) {
    System.err.println("ERROR: ${logMessage()}")
}

actual inline fun error(error: Throwable, logMessage: () -> String) {
    System.err.println("ERROR: ${logMessage()} ${error.message}")
}

actual inline fun info(logMessage: () -> String) {
    System.err.println("INFO: ${logMessage()}")
}

actual inline fun verbose(logMessage: () -> String) {
    System.err.println("VERBOSE: ${logMessage()}")
}

actual inline fun warn(logMessage: () -> String) {
    System.err.println("WARN: ${logMessage()}")
}

actual inline fun debug(logMessage: () -> String) {
    System.err.println("DEBUG: ${logMessage()}")
}

actual inline fun wtf(logMessage: () -> String) {
    System.err.println("WTF: ${logMessage()}")
}
