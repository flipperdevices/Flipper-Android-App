package com.flipperdevices.core.share

import okio.Path

/**
 * This class should be only created via [PlatformShareHelper] due
 * to permission restrictions. On android we can share files only in
 * specified folder specified in AndroidManifest
 */
class PlatformSharableFile internal constructor(val path: Path)
