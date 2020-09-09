package com.flipper.core.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.bluelinelabs.conductor.Controller
import moxy.MvpDelegate
import moxy.MvpDelegateHolder

abstract class BaseController<VB : ViewBinding> : MvpDelegateHolder, Controller {
    // Lazy used for prevent leaking `this`
    private var mvpDelegate: MvpDelegate<BaseController<VB>>? = null
    private var isStateSaved = false

    //
    // Hack to initialize moxy
    //
    // mvpDelegate.onCreate should be called before view creation.
    // Because of that it called inside onRestoreInstanceState or before onCreateView methods.
    //
    // Why not using a constructor initialization?
    // In constructor args, conductor doesn't provide information
    // about saved instance state. That information is provided via onRestoreInstanceState method.
    // Because of that we can't call mvpDelegate.onCreate in constructor. If we call it in
    // constructor we will create new instance of presenter.
    //
    // Why do we need isCreated field?
    // Moxy doesn't have isInitialized field inside. So if we call inCreate twice moxy
    // will be initialized two times. The first initialisation will be overridden by second.
    //
    // Why are there 2 ways to initialize moxy?
    // The controller can be in backstack in that case onCreateView method will be never called and
    // moxy will be never initialized. If we call mvpDelegate.onSaveInstanceState on
    // not initialized moxy, we will receive NPE.
    //
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
            getMvpDelegate().onCreate()
        }
        _binding = getViewInflater().invoke(inflater, container, false)
        val rootView = _binding!!.root
        initializeView()
        return rootView
    }

    final override fun onAttach(view: View) {
        getMvpDelegate().onAttach()
    }

    abstract fun getViewInflater(): ViewInflater<VB>

    abstract fun initializeView()
    protected open fun disposeView() = Unit

    final override fun onDetach(view: View) {
        getMvpDelegate().onDetach()
    }

    final override fun onDestroyView(view: View) {
        disposeView()
        _binding = null
        getMvpDelegate().onDestroyView()
    }

    final override fun onDestroy() {
        // If state is saved, it means controller rotation.
        // Do not destroy presenter.
        if (isStateSaved) return
        getMvpDelegate().onDestroy()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        isStateSaved = true
        getMvpDelegate().onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        isCreated = true
        getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun getMvpDelegate(): MvpDelegate<*> {
        if (mvpDelegate == null) {
            mvpDelegate = MvpDelegate(this)
        }
        return mvpDelegate!!
    }
}
