package com.flipper.integrationtest

import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater
import com.flipper.integrationtest.databinding.ControllerCounterBinding
import moxy.ktx.moxyPresenter

class CounterController : BaseController<ControllerCounterBinding>(),
    CounterView {
    val presenter by moxyPresenter { CounterPresenter() }

    override fun initializeView() {
        binding.incrementButton.setOnClickListener {
            presenter.clickOnNumber()
        }
    }


    override fun setNumber(number: Int) {
        binding.incrementButton.text = "$number"
    }

    override fun getViewInflater(): ViewInflater<ControllerCounterBinding> {
        return ControllerCounterBinding::inflate
    }
}
