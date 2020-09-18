package com.flipper.app.home.ui

import com.flipper.app.home.ui.data.HomeTab
import moxy.MvpPresenter
import javax.inject.Inject

class HomePresenter @Inject constructor() : MvpPresenter<HomeView>() {
    override fun onFirstViewAttach() {
        viewState.switchTabTo(HomeTab.UserSpace)
    }

    fun onSwitchTabClick(switchTo: HomeTab) {
        viewState.switchTabTo(switchTo)
    }
}
