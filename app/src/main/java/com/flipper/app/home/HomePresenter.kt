package com.flipper.app.home

import moxy.MvpPresenter

class HomePresenter : MvpPresenter<HomeView>() {
  fun onShowDialogClick() {
    viewState.showDialog()
  }

  fun onHideDialog() {
    viewState.hideDialog()
  }
}
