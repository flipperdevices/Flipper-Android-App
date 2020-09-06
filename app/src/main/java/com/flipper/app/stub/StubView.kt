package com.flipper.app.stub

import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SingleStateStrategy::class)
interface StubView : MvpView {
    fun setNumber(number: Int)
}
