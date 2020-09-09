package com.flipper.app.stub

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface StubView : MvpView {
    fun setNumber(number: Int)
}
