package com.flipper.integrationtest

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout

class TestActivity : Activity() {
    lateinit var contentView: FrameLayout
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentView = FrameLayout(this)
        contentView.id = View.generateViewId()
        setContentView(contentView)
    }
}
