package com.flipperdevices.core.permission.impl

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.permission.api.PermissionListener
import com.flipperdevices.core.permission.api.PermissionRequestHandler
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, PermissionRequestHandler::class)
class PermissionRequestHandlerImpl @Inject constructor() :
    PermissionRequestHandler,
    Application.ActivityLifecycleCallbacks,
    ActivityResultCallback<Map<String, @JvmSuppressWildcards Boolean>> {

    private var permissionRequest: ActivityResultLauncher<Array<String>>? = null
    private val permissionPendingListeners = mutableMapOf<String, List<PermissionListener>>()

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is AppCompatActivity) {
            permissionRequest = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(), this
            )
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        permissionRequest = null
        permissionPendingListeners.clear()
    }

    override fun onActivityResult(result: Map<String, Boolean>) {
        result.forEach { (permission, result) ->
            permissionPendingListeners.remove(permission)?.forEach { listener ->
                listener.invoke(permission, result)
            }
        }
    }

    override fun requestPermission(vararg permissions: String, listener: PermissionListener) {
        val request = permissionRequest
        if (request == null) {
            permissions.forEach {
                listener(it, false)
            }
            return
        }
        permissions.forEach { permission ->
            val currentList = permissionPendingListeners.getOrDefault(permission, listOf())
            permissionPendingListeners[permission] = currentList + listener
        }
        request.launch(permissions.toList().toTypedArray())
    }

    // Unused fun
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
