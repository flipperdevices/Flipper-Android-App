package com.flipper.app.stub

import android.os.Bundle
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.app.databinding.ControllerStubBinding
import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater
import moxy.presenter.InjectPresenter

class StubController(args: Bundle) : BaseController<ControllerStubBinding>(args),
    StubView {
    @InjectPresenter
    lateinit var presenter: StubPresenter

    constructor(text: String) : this(bundleOf("TEXT" to text))

    override fun initializeView() {
        val text = args.getString("TEXT")
        binding.stubButton.text = text
        binding.stubButton.setOnClickListener {
            router.pushController(RouterTransaction.with(StubController("$text - child")))
        }
        binding.numberButton.setOnClickListener {
            presenter.clickOnNumber()
        }
    }


    override fun setNumber(number: Int) {
        binding.numberButton.text = "Number $number"
    }

    override fun getViewInflater(): ViewInflater<ControllerStubBinding> {
        return ControllerStubBinding::inflate
    }
}
