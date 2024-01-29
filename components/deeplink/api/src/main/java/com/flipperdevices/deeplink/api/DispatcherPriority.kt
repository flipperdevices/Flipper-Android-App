package com.flipperdevices.deeplink.api

/**
 * With this priority we understand how much we have to respect the DeepLinkHandler response
 * For example: we can load all files in FileManager,
 * but the files ibtn and others must be processed by the archive first.
 *
 * Enum order is important
 */
enum class DispatcherPriority {
    LOW,
    DEFAULT,
    HIGH,
}
