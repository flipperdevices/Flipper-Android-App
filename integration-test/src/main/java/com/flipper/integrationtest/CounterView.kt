package com.flipper.integrationtest

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface CounterView : MvpView {
    fun setNumber(number: Int)
}
