package com.flipper.app.home

import androidx.appcompat.app.AlertDialog
import com.flipper.app.databinding.ControllerHomeBinding
import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater
import moxy.presenter.InjectPresenter

class HomeController : BaseController<ControllerHomeBinding>(), HomeView {
  override fun getViewInflater(): ViewInflater<ControllerHomeBinding> {
    return ControllerHomeBinding::inflate
  }

  @InjectPresenter
  lateinit var presenter: HomePresenter
  private var dialog: AlertDialog? = null

  override fun initializeView() {
    dialog = AlertDialog.Builder(binding.root.context)
      .setTitle("Hello")
      .setMessage("Flipper")
      .setOnDismissListener { presenter.onHideDialog() }
      .create()

    binding.showDialogButton.setOnClickListener {
      presenter.onShowDialogClick()
    }
  }

  override fun disposeView() {
    dialog?.hide()
    dialog = null
  }

  override fun showDialog() {
    dialog?.show()
  }

  override fun hideDialog() {
    dialog?.hide()
  }
}
