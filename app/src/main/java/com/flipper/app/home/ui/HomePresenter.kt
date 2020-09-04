package com.flipper.app.home.ui

import com.flipper.app.home.ui.data.HomeTab
import moxy.MvpPresenter
import javax.inject.Inject

class HomePresenter @Inject constructor() : MvpPresenter<HomeView>() {
    fun onSwitchTabClick(switchTo: HomeTab) {
        viewState.switchTabTo(switchTo)
    }
}
