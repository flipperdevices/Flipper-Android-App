package com.flipperdevices.info.impl.fragment

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.parcelable
import com.flipperdevices.core.ktx.android.withArgs
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.core.ui.fragment.ComposeFragment
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.info.impl.api.InfoFeatureEntry
import com.flipperdevices.info.impl.compose.InfoNavigation
import com.flipperdevices.info.impl.di.InfoComponent
import kotlinx.collections.immutable.toImmutableSet
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

class InfoFragment : ComposeFragment(), OnBackPressListener {
    @Inject
    lateinit var featureEntries: MutableSet<AggregateFeatureEntry>

    @Inject
    lateinit var infoFeatureEntry: InfoFeatureEntry

    private var navController: NavHostController? = null

    private val deeplink: Deeplink?
        get() = arguments?.parcelable(DeeplinkConstants.KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        navController = rememberNavController()
        navController?.let {
            LaunchedEffect(key1 = Unit) {
                if (deeplink == null) return@LaunchedEffect
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = deeplink?.intent?.data
                    putExtra("deeplink", deeplink)
                }
                it.handleDeepLink(intent = intent)
            }
            InfoNavigation(
                navController = it,
                featureEntries = featureEntries.toImmutableSet(),
                infoFeatureEntry = infoFeatureEntry
            )
        }
    }

    override fun onBackPressed(): Boolean {
        navController?.let {
            val currentDestination = it.currentDestination ?: return false
            if (currentDestination.route == infoFeatureEntry.ROUTE.name) return false
            it.popBackStack()
            return true
        }
        return false
    }

    override fun getStatusBarColor(): Int = DesignSystem.color.accent

    companion object {
        fun newInstance(deeplink: Deeplink?): InfoFragment {
            return InfoFragment().withArgs {
                putParcelable(DeeplinkConstants.KEY, deeplink)
            }
        }
    }
}
