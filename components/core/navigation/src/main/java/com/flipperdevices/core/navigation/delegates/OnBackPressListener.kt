package com.flipperdevices.core.navigation.delegates

interface OnBackPressListener {
    /**
     * @return true if we can process it itself
     */
    fun onBackPressed(): Boolean
}
