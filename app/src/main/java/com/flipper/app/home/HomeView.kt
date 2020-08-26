package com.flipper.app.home

import moxy.MvpView
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SingleStateStrategy::class)
interface HomeView : MvpView {
  fun showDialog()
  fun hideDialog()
}
