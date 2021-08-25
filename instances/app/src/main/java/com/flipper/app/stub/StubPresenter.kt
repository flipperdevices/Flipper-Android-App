package com.flipper.app.stub

import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class StubPresenter : MvpPresenter<StubView>() {
    private var i = 0

    fun clickOnNumber() {
        viewState.setNumber(i++)
    }
}
