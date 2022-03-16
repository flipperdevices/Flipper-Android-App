package com.flipperdevices.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.FontFamilyFactory.getTypographyWithReplacedFontFamily
import com.flipperdevices.core.ui.composable.LocalRouter

/**
 * Fragment with jetpack compose support
 */
abstract class ComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    typography = getTypographyWithReplacedFontFamily(
                        FontFamilyFactory.Roboto,
                        MaterialTheme.typography
                    )
                ) {
                    CompositionLocalProvider(LocalRouter provides requireRouter()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            RenderView()
                        }
                    }
                }
            }
        }
    }

    /**
     * Please, don't forget add composable annotation to override funs
     */
    @Composable
    abstract fun RenderView()
}
