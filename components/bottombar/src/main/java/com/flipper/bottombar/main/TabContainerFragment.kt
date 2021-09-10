package com.flipper.bottombar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flipper.bottombar.R
import com.flipper.bottombar.di.BottomBarComponent
import com.flipper.bottombar.model.FlipperBottomTab
import com.flipper.bottombar.navigate.ScreenTabProvider
import com.flipper.core.di.ComponentHolder
import com.flipper.core.navigation.delegates.OnBackPressListener
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class TabContainerFragment : Fragment(), OnBackPressListener {

    private val navigator: Navigator by lazy {
        AppNavigator(requireActivity(), R.id.container, childFragmentManager)
    }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screenTabProvider: ScreenTabProvider

    private val containerTab: FlipperBottomTab
        get() = requireArguments().getSerializable(EXTRA_NAME) as FlipperBottomTab

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
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
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