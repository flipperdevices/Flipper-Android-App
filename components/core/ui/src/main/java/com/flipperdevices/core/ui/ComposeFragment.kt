package com.flipperdevices.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.flipperdevices.core.ui.FontFamilyFactory.getTypographyWithReplacedFontFamily

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
                    RenderView()
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
