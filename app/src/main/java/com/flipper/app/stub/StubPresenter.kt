package com.flipper.app.stub

import moxy.MvpPresenter

class StubPresenter : MvpPresenter<StubView>() {
    private var i = 0

    fun clickOnNumber() {
        viewState.setNumber(i++)
    }
}
