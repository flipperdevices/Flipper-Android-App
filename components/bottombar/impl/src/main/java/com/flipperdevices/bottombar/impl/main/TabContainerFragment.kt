package com.flipperdevices.bottombar.impl.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.di.BottomBarComponent
import com.flipperdevices.bottombar.impl.main.subnavigation.LocalCiceroneHolder
import com.flipperdevices.bottombar.impl.main.subnavigation.OnDoublePressOnTab
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.bottombar.impl.navigate.ScreenTabProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.flipperdevices.core.ui.fragment.provider.StatusBarColorProvider
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class TabContainerFragment :
    Fragment(),
    OnBackPressListener,
    RouterProvider,
    OnDoublePressOnTab,
    StatusBarColorProvider {

    private val navigator: Navigator by lazy {
        AppNavigator(requireActivity(), R.id.container, childFragmentManager)
    }

    @Inject
    lateinit var screenTabProvider: ScreenTabProvider

    @Inject
    lateinit var ciceroneHolder: LocalCiceroneHolder

    private val containerTab: FlipperBottomTab
        get() = requireArguments().getSerializable(EXTRA_NAME) as FlipperBottomTab

    private val cicerone: Cicerone<Router> by lazy { ciceroneHolder.getCicerone(containerTab) }
    override val router: Router by lazy { cicerone.router }

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentHolder.component<BottomBarComponent>().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.container) == null) {
            router.replaceScreen(screenTabProvider.getScreen(containerTab))
        }
    }

    override fun onResume() {
        super.onResume()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onBackPressed(): Boolean {
        val fragment = childFragmentManager.findFragmentById(R.id.container)
        return if ((fragment as? OnBackPressListener)?.onBackPressed() == true) {
            true
        } else {
            router.exit()
            true
        }
    }

    override fun getStatusBarColor(): Int? {
        val fragment = childFragmentManager.findFragmentById(R.id.container)
        if (fragment !is StatusBarColorProvider) {
            return null
        }

        return fragment.getStatusBarColor()
    }

    override fun onDoublePress() {
        router.newRootScreen(screenTabProvider.getScreen(containerTab))
    }

    companion object {
        private const val EXTRA_NAME = "tab_extra_name"

        fun getNewInstance(tab: FlipperBottomTab) =
            TabContainerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_NAME, tab)
                }
            }
    }
}
