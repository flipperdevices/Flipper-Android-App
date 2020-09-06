package com.flipper.core.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.bluelinelabs.conductor.Controller
import moxy.MvpDelegate

abstract class BaseController<VB : ViewBinding> : Controller {
    // Lazy used for prevent leaking `this`
    private val mvpDelegate by lazy(LazyThreadSafetyMode.NONE) { MvpDelegate(this) }
    private var isStateSaved = false
    private var isCreated = false

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding
            ?: error("attempt to get binding before onCreateView or after onDestroyView")

    constructor()
    constructor(args: Bundle) : super(args)

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        if (!isCreated) {
            isCreated = true
            mvpDelegate.onCreate()
        }
        _binding = getViewInflater().invoke(inflater, container, false)
        val rootView = _binding!!.root
        initializeView()
        return rootView
    }

    final override fun onAttach(view: View) {
        mvpDelegate.onAttach()
    }

    abstract fun getViewInflater(): ViewInflater<VB>

    abstract fun initializeView()
    protected open fun disposeView() = Unit

    final override fun onDetach(view: View) {
        mvpDelegate.onDetach()
    }

    final override fun onDestroyView(view: View) {
        disposeView()
        _binding = null
        mvpDelegate.onDestroyView()
    }

    final override fun onDestroy() {
        // If state is saved, it means controller rotation.
        // Do not destroy presenter.
        if (isStateSaved) return
        mvpDelegate.onDestroy()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        isStateSaved = true
        mvpDelegate.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        isCreated = true
        mvpDelegate.onCreate(savedInstanceState)
    }
}
