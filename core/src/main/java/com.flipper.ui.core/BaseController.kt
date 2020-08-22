package com.flipper.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.bluelinelabs.conductor.Controller

abstract class BaseController<VB : ViewBinding> : Controller {
  private var bindingInternal: VB? = null
  protected val binding: VB
    get() = bindingInternal
      ?: error("attempt to get binding before onCreateView or after onDestroyView")

  constructor()
  constructor(args: Bundle) : super(args)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup,
    savedViewState: Bundle?
  ): View {
    bindingInternal = getViewInflater().invoke(inflater, container, false)
    val rootView = bindingInternal!!.root
    initializeView()
    return rootView
  }

  abstract fun getViewInflater(): ViewInflater<VB>

  abstract fun initializeView()
  protected open fun destroyView() = Unit

  final override fun onDestroyView(view: View) {
    destroyView()
    bindingInternal = null
    super.onDestroyView(view)
  }
}
