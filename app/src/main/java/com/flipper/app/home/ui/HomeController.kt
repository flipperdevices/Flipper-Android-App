package com.flipper.app.home.ui

import com.flipper.app.FlipperApplication
import com.flipper.app.databinding.ControllerHomeBinding
import com.flipper.app.home.di.DaggerHomeScreenComponent
import com.flipper.app.home.ui.data.HomeTab
import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class HomeController : BaseController<ControllerHomeBinding>(), HomeView {
    @InjectPresenter
    lateinit var presenter: HomePresenter

    @ProvidePresenter
    fun providePresenter(): HomePresenter {
        return DaggerHomeScreenComponent.builder()
            .homeScreenDependencies(FlipperApplication.component)
            .build()
            .presenter()
    }

    override fun getViewInflater(): ViewInflater<ControllerHomeBinding> {
        return ControllerHomeBinding::inflate
    }

    override fun initializeView() {
    }

    override fun switchTabTo(switchTo: HomeTab) {
    }
}
