package com.flipperdevices.pair.impl.fragments.guide

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.pair.impl.composable.guide.ComposableGuide
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.navigation.machine.PairScreenStateDispatcher
import javax.inject.Inject

class FragmentGuide : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        ComposableGuide(onNextClickListener = {
            stateDispatcher.invalidateCurrentState { it.copy(guidePassed = true) }
        })
    }
}
