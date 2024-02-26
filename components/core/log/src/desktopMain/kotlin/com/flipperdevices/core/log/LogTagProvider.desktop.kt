package com.flipperdevices.core.log

actual inline fun LogTagProvider.error(logMessage: () -> String) {
    System.err.println("[$TAG] ERROR: ${logMessage()}")
}

actual inline fun LogTagProvider.error(error: Throwable?, logMessage: () -> String) {
    System.err.println("[$TAG] ERROR: ${logMessage()} ${error?.message}")
}

actual inline fun LogTagProvider.info(logMessage: () -> String) {
    System.err.println("[$TAG] INFO: ${logMessage()}")
}

actual inline fun LogTagProvider.verbose(logMessage: () -> String) {
    System.err.println("[$TAG] VERBOSE: ${logMessage()}")
}

actual inline fun LogTagProvider.warn(logMessage: () -> String) {
    System.err.println("[$TAG] WARN: ${logMessage()}")
}

actual inline fun LogTagProvider.debug(logMessage: () -> String) {
    System.err.println("[$TAG] DEBUG: ${logMessage()}")
}

actual inline fun LogTagProvider.wtf(logMessage: () -> String) {
    System.err.println("[$TAG] WTF: ${logMessage()}")
}
