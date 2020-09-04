package com.flipper.app.stub

import android.os.Bundle
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.app.databinding.ControllerStubBinding
import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater

class StubController(args: Bundle) : BaseController<ControllerStubBinding>(args) {
    constructor(text: String) : this(bundleOf("TEXT" to text))

    override fun initializeView() {
        val text = args.getString("TEXT")
        binding.stubButton.text = text
        binding.stubButton.setOnClickListener {
            router.pushController(RouterTransaction.with(StubController("$text - child")))
        }
    }

    override fun getViewInflater(): ViewInflater<ControllerStubBinding> {
        return ControllerStubBinding::inflate
    }
}
