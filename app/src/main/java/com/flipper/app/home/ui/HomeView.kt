package com.flipper.app.home.ui

import com.flipper.app.home.ui.data.HomeTab
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface HomeView : MvpView {
    fun switchTabTo(switchTo: HomeTab)
}
