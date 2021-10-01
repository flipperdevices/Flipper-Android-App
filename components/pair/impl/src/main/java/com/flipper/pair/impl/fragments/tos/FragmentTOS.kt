package com.flipper.pair.impl.fragments.tos

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipper.core.di.ComponentHolder
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.impl.composable.tos.ComposableTOS
import com.flipper.pair.impl.di.PairComponent
import com.flipper.pair.impl.navigation.machine.PairScreenStateDispatcher
import com.flipper.pair.impl.navigation.storage.PairStateStorage
import javax.inject.Inject

class FragmentTOS : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    @Inject
    lateinit var stateStorage: PairStateStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        ComposableTOS { onAccept() }
    }

    private fun onAccept() {
        stateStorage.markTosPassed()
        stateDispatcher.invalidateCurrentState { it.copy(tosAccepted = true) }
    }
}
