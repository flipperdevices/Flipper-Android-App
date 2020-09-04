package com.flipper.app.home.ui

import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.app.FlipperApplication
import com.flipper.app.databinding.ControllerHomeBinding
import com.flipper.app.home.di.DaggerHomeScreenComponent
import com.flipper.app.home.ui.data.HomeTab
import com.flipper.app.stub.StubController
import com.flipper.core.view.BaseController
import com.flipper.core.view.ViewInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import timber.log.Timber

class HomeController :
    BaseController<ControllerHomeBinding>(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    HomeView {

    @IdRes
    private var selectedTabId = NO_TAB_ID
    private val tabRouterStates = SparseArray<Bundle>()
    private lateinit var childRouter: Router

    override fun initializeView() {
        childRouter = getChildRouter(binding.homeContainer)
        binding.homeBottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun switchTabTo(switchTo: HomeTab) {
        saveStateFromTab(selectedTabId)
        selectedTabId = switchTo.menuItemId
        binding.homeBottomNavigation.selectedItemId = switchTo.menuItemId
        clearChildRouterContainer()
        val savedTabState: Bundle? = tabRouterStates.get(switchTo.menuItemId)

        if (savedTabState != null) {
            childRouter.restoreInstanceState(savedTabState)
            childRouter.rebindIfNeeded()
        } else {
            val controller: Controller = when (switchTo) {
                HomeTab.UserSpace -> StubController("UserSpace")
                HomeTab.Market -> StubController("Market")
                HomeTab.Settings -> StubController("Settings")
            }
            childRouter.setRoot(RouterTransaction.with(controller))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == selectedTabId) return true
        val homeTab = HomeTab.createFromMenuItemId(item.itemId)
        return if (homeTab == null) {
            false
        } else {
            presenter.onSwitchTabClick(homeTab)
            true
        }
    }

    private fun saveStateFromTab(@IdRes tabId: Int) {
        if (tabId == NO_TAB_ID) {
            Timber.w("ignore state saving from tab with NO_TAB_ID")
            return
        }

        val tabRouteBundle = Bundle()
        childRouter.saveInstanceState(tabRouteBundle)
        tabRouterStates.put(tabId, tabRouteBundle)
    }

    private fun clearChildRouterContainer() {
        childRouter.setPopsLastView(true)
        childRouter.popToRoot()
        if (childRouter.hasRootController()) childRouter.popCurrentController()
        childRouter.setPopsLastView(false)
    }

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
}

private const val NO_TAB_ID = -1
