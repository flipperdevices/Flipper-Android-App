package com.flipper.app.home.ui

import com.flipper.app.home.ui.data.HomeTab
import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SingleStateStrategy::class)
interface HomeView : MvpView {
    fun switchTabTo(switchTo: HomeTab)
}
