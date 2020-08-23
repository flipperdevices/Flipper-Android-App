package com.flipper.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.flipper.app.databinding.HomeLayoutBinding
import com.flipper.ui.core.BaseController
import com.flipper.ui.core.ViewInflater
import timber.log.Timber

internal class HomeController : BaseController<HomeLayoutBinding>() {
  override fun getViewInflater(): ViewInflater<HomeLayoutBinding> = HomeLayoutBinding::inflate

  init {
    addLifecycleListener(object : LifecycleListener {
      override fun onChangeStart(
        controller: Controller,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
      ) {
        Timber.tag("HomeController").i("onChangeStart")
      }

      override fun onChangeEnd(
        controller: Controller,
        changeHandler: ControllerChangeHandler,
        changeType: ControllerChangeType
      ) {
        Timber.tag("HomeController").i("onChangeEnd")
      }

      override fun preCreateView(controller: Controller) {
        Timber.tag("HomeController").i("preCreateView")
      }

      override fun postCreateView(controller: Controller, view: View) {
        Timber.tag("HomeController").i("postCreateView")
      }

      override fun preAttach(controller: Controller, view: View) {
        Timber.tag("HomeController").i("preAttach")
      }

      override fun postAttach(controller: Controller, view: View) {
        Timber.tag("HomeController").i("postAttach")
      }

      override fun preDetach(controller: Controller, view: View) {
        Timber.tag("HomeController").i("preDetach")
      }

      override fun postDetach(controller: Controller, view: View) {
        Timber.tag("HomeController").i("postDetach")
      }

      override fun preDestroyView(controller: Controller, view: View) {
        Timber.tag("HomeController").i("preDestroyView")
      }

      override fun postDestroyView(controller: Controller) {
        Timber.tag("HomeController").i("postDestroyView")
      }

      override fun preDestroy(controller: Controller) {
        Timber.tag("HomeController").i("preDestroy")
      }

      override fun postDestroy(controller: Controller) {
        Timber.tag("HomeController").i("postDestroy")
      }

      override fun preContextAvailable(controller: Controller) {
        Timber.tag("HomeController").i("preContextAvailable")
      }

      override fun postContextAvailable(controller: Controller, context: Context) {
        Timber.tag("HomeController").i("postContextAvailable")
      }

      override fun preContextUnavailable(controller: Controller, context: Context) {
        Timber.tag("HomeController").i("preContextUnavailable")
      }

      override fun postContextUnavailable(controller: Controller) {
        Timber.tag("HomeController").i("postContextUnavailable")
      }

      override fun onSaveInstanceState(controller: Controller, outState: Bundle) {
        Timber.tag("HomeController").i("onSaveInstanceState")
      }

      override fun onRestoreInstanceState(controller: Controller, savedInstanceState: Bundle) {
        Timber.tag("HomeController").i("onRestoreInstanceState")
      }

      override fun onSaveViewState(controller: Controller, outState: Bundle) {
        Timber.tag("HomeController").i("onSaveViewState")
      }

      override fun onRestoreViewState(controller: Controller, savedViewState: Bundle) {
        Timber.tag("HomeController").i("onRestoreViewState")
      }
    })
  }

  override fun initializeView() {
  }
}
