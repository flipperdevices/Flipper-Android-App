package com.flipperdevices.core.ktx.jre

/**
 * @param items array objects
 * @return is there a null object in the array
 */
fun isNotNull(vararg items: Any?): Boolean {
    return items.none { it == null }
}
