package com.flipper.app.home

import com.flipper.app.home.repository.ProfileRepository
import moxy.MvpPresenter
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val repository: ProfileRepository
) : MvpPresenter<HomeView>() {
    fun onShowDialogClick() {
        viewState.showDialog()
    }

    fun onHideDialog() {
        viewState.hideDialog()
    }
}
