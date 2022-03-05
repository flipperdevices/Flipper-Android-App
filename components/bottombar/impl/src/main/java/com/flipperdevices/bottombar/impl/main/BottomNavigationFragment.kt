package com.flipperdevices.bottombar.impl.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.databinding.FragmentBottombarBinding
import com.flipperdevices.bottombar.impl.di.BottomBarComponent
import com.flipperdevices.bottombar.impl.main.compose.ComposeBottomBar
import com.flipperdevices.bottombar.impl.main.service.BottomNavigationViewModel
import com.flipperdevices.bottombar.impl.main.subnavigation.OnDoublePressOnTab
import com.flipperdevices.bottombar.impl.main.viewmodel.InAppNotificationState
import com.flipperdevices.bottombar.impl.main.viewmodel.InAppNotificationViewModel
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.inappnotification.api.model.InAppNotification
import javax.inject.Inject

class BottomNavigationFragment : Fragment(), OnBackPressListener {
    private val bottomNavigationViewModel by viewModels<BottomNavigationViewModel>()
    private val notificationViewModel by viewModels<InAppNotificationViewModel>()

    private lateinit var binding: FragmentBottombarBinding

    @Inject
    lateinit var notificationRenderer: InAppNotificationRenderer

    init {
        ComponentHolder.component<BottomBarComponent>().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottombarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomBar.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val selectedItem by bottomNavigationViewModel.selectedTab.collectAsState()
                ComposeBottomBar(selectedItem) {
                    selectTab(it)
                }
            }
        }
        binding.inappNotification.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val notificationState by notificationViewModel.state().collectAsState()
                val localNotificationState = notificationState
                if (localNotificationState !is InAppNotificationState.ShownNotification) {
                    return@setContent
                }
                notificationRenderer.InAppNotification(localNotificationState.notification) {
                    notificationViewModel.onNotificationHidden(localNotificationState.notification)
                }
            }
        }

        if (childFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            selectTab(FlipperBottomTab.STORAGE)
        }
    }

    override fun onResume() {
        super.onResume()
        notificationViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        notificationViewModel.onPause()
    }

    private fun selectTab(tab: FlipperBottomTab) {
        bottomNavigationViewModel.onSelectTab(tab)
        val fm = childFragmentManager
        val tabName = tab.name
        val currentFragment: Fragment? = fm.fragments.find { it.isVisible }
        val newFragment = fm.findFragmentByTag(tabName)
        if (currentFragment != null && newFragment != null && currentFragment === newFragment) {
            if (currentFragment is OnDoublePressOnTab) {
                currentFragment.onDoublePress()
            }
            return
        }
        val transaction = fm.beginTransaction()
        if (newFragment == null) {
            transaction.add(
                R.id.fragment_container,
                TabContainerFragment.getNewInstance(tab),
                tabName
            )
        }
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        if (newFragment != null) {
            transaction.show(newFragment)
        }
        transaction.commitNow()
    }

    override fun onBackPressed(): Boolean {
        val currentFragment = childFragmentManager.fragments.find { it.isVisible } ?: return false
        return (currentFragment as? OnBackPressListener)?.onBackPressed() ?: false
    }
}
