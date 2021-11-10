package com.flipperdevices.core.navigation

import androidx.fragment.app.Fragment
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.github.terrakok.cicerone.Router

fun Fragment.requireRouter(): Router {
    return (parentFragment as RouterProvider).router
}
