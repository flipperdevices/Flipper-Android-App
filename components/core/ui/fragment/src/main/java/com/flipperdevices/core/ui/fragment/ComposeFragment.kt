package com.flipperdevices.core.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.flipperdevices.core.ktx.android.setStatusBarColor
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.fragment.provider.StatusBarColorProvider
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.theme.FlipperTheme

/**
 * Fragment with jetpack compose support
 */
abstract class ComposeFragment : Fragment(), StatusBarColorProvider {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeViewRenderWithTheme()
            }
        }
    }

    @Composable
    fun ComposeViewRenderWithTheme() {
        FlipperTheme(content = {
            CompositionLocalProvider(LocalRouter provides requireRouter()) {
                SubComposeWrapper(modifier = Modifier.fillMaxSize()) {
                    RenderView()
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(getStatusBarColor())
    }

    override fun getStatusBarColor(): Int? = null

    /**
     * Please, don't forget add composable annotation to override funs
     */
    @Composable
    abstract fun RenderView()
}

/**
 * It's a kind of magic.
 * It's not entirely clear what the problem is,
 * but I suspect that it's because the usual Compose Layout doesn't change its size dynamically,
 * while subcompose does.
 * Fixes a problem when screen components go over the edge of the screen.
 */
@Composable
private fun SubComposeWrapper(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    SubcomposeLayout(modifier) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        layout(layoutWidth, layoutHeight) {
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
            subcompose(null) {
                content()
            }.map { it.measure(looseConstraints) }.forEach {
                it.place(0, 0)
            }
        }
    }
}
