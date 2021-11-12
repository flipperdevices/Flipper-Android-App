package com.flipperdevices.core.navigation

import androidx.fragment.app.Fragment
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.github.terrakok.cicerone.Router

fun Fragment.requireRouter(): Router {
    if (parentFragment != null && parentFragment is RouterProvider) {
        return (parentFragment as RouterProvider).router
    }
    if (activity != null && activity is RouterProvider) {
        return (activity as RouterProvider).router
    }
    error("Parent activity and fragment is not RouterProvider")
}
