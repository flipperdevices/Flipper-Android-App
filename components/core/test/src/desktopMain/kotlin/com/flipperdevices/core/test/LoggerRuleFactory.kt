package com.flipperdevices.core.test

actual object LoggerRuleFactory {
    actual fun create(): LoggerRule {
        return object : LoggerRule() {
        }
    }
}
