package com.flipperdevices.buildlogic.property

import org.gradle.api.plugins.ExtensionContainer

interface PropertyValue {
    val key: String
    fun getValue(): Result<String>
}

// Don't replace extensionContainer with project
// Different projects have different extensionContainer
fun PropertyValue.asCached(extensionContainer: ExtensionContainer): PropertyValue {
    return CachedPropertyValue(
        extensionContainer = extensionContainer,
        propertyValue = this
    )
}
