package com.flipperdevices.pair.impl.navigation.machine

import android.content.Context
import android.os.Build
import com.flipperdevices.bridge.api.utils.DeviceFeatureHelper
import com.flipperdevices.core.api.BottomNavigationActivityApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.pair.impl.navigation.models.PairNavigationScreens
import com.flipperdevices.pair.impl.navigation.models.PairScreenState
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.squareup.anvil.annotations.ContributesBinding
import java.util.Stack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class PairScreenStateDispatcherImpl @Inject constructor(
    private val router: Router,
    private val context: Context,
    private val bottomNavigationActivityApi: BottomNavigationActivityApi
) : PairScreenStateDispatcher {
    // Exclude currentState
    private val stateStack = Stack<PairScreenState>()

    private val listeners = mutableListOf<ScreenStateChangeListener>()
    private var currentState: PairScreenState? = null
        set(value) {
            field = value
            if (value != null) {
                listeners.forEach { it.onStateChanged(value) }
            }
        }

    @Synchronized
    override fun invalidateCurrentState(stateChanger: (PairScreenState) -> PairScreenState) {
        val currentNonNullState = currentState ?: error("Current state is null")
        invalidate(stateChanger(currentNonNullState))
    }

    @Synchronized
    override fun invalidate(state: PairScreenState) {
        if (currentState == state) {
            // Do nothing, because we already on this state
            return
        }
        currentState = state
        val screen = getScreenForStateUnsafe(state)
        if (screen == null) {
            bottomNavigationActivityApi.openBottomNavigationScreen()
            return
        }
        if (currentState != null) {
            stateStack.push(currentState)
        }
        router.replaceScreen(screen)
    }

    @Synchronized
    override fun back() {
        if (stateStack.empty()) {
            return
        }
        val prevState = stateStack.pop()
        val screen = getScreenForStateUnsafe(prevState) ?: error("Call back on finish state")
        currentState = prevState
        router.replaceScreen(screen)
    }

    override fun addStateListener(stateListener: ScreenStateChangeListener) {
        if (!listeners.contains(stateListener)) {
            listeners.add(stateListener)
        }
    }

    override fun removeStateListener(stateListener: ScreenStateChangeListener) {
        listeners.remove(stateListener)
    }

    /**
     * This fun is unsafe and will not check which state we switch
     * Call it only after checks all requirement
     */
    @Suppress("ReturnCount")
    private fun getScreenForStateUnsafe(state: PairScreenState): Screen? {
        if (!state.tosAccepted) {
            return PairNavigationScreens.tosScreen()
        }
        // Device connection
        if (!state.guidePassed) {
            return PairNavigationScreens.guideScreen()
        }
        if (!state.permissionGranted) {
            return PairNavigationScreens.permissionScreen()
        }
        if (!state.devicePaired) {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                DeviceFeatureHelper.isCompanionFeatureAvailable(context)
            ) {
                PairNavigationScreens.companionPairScreen()
            } else {
                PairNavigationScreens.standardPairScreen()
            }
        }
        // State Machine finish
        return null
    }
}
