package com.flipper.pair.impl.navigation.machine

import android.content.Context
import android.os.Build
import com.flipper.bridge.utils.DeviceFeatureHelper
import com.flipper.core.api.BottomNavigationActivityApi
import com.flipper.core.di.AppGraph
import com.flipper.pair.impl.navigation.models.PairNavigationScreens
import com.flipper.pair.impl.navigation.models.PairScreenState
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
    private var currentState: PairScreenState? = null

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
        val screen = getScreenForStateUnsafe(state)
        if (screen == null) {
            bottomNavigationActivityApi.openBottomNavigationScreen()
            return
        }
        if (currentState != null) {
            stateStack.push(currentState)
        }
        router.replaceScreen(screen)
        currentState = state
    }

    @Synchronized
    override fun back() {
        if (stateStack.empty()) {
            return
        }
        val prevState = stateStack.pop()
        val screen = getScreenForStateUnsafe(prevState) ?: error("Call back on finish state")
        router.replaceScreen(screen)
        currentState = prevState
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
