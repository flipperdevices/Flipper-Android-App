package com.flipper.integrationtest

import moxy.MvpPresenter

import moxy.InjectViewState

@InjectViewState
class CounterPresenter : MvpPresenter<CounterView>() {
    private var i = 0

    override fun onFirstViewAttach() {
        viewState.setNumber(i)
    }

    fun clickOnNumber() {
        viewState.setNumber(i++)
    }
}
