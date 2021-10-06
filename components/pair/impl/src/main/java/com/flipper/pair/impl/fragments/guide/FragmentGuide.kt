package com.flipper.pair.impl.fragments.guide

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipper.core.di.ComponentHolder
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.impl.composable.guide.ComposableGuide
import com.flipper.pair.impl.di.PairComponent
import com.flipper.pair.impl.navigation.machine.PairScreenStateDispatcher
import javax.inject.Inject

class FragmentGuide : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        ComposableGuide(onNextClickListener = {
            stateDispatcher.invalidateCurrentState { it.copy(guidePassed = true) }
        })
    }
}
