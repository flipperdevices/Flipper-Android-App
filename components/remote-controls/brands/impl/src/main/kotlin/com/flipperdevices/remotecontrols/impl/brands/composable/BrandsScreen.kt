package com.flipperdevices.remotecontrols.impl.brands.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadedContent
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadingComposable
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.brands.impl.R as BrandsR
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.searchbar.ComposableSearchBar
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun BrandsScreen(
    brandsDecomposeComponent: BrandsDecomposeComponent,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val model by remember(brandsDecomposeComponent, coroutineScope) {
        brandsDecomposeComponent.model(coroutineScope)
    }.collectAsState()
    var isQueryShown by rememberSaveable(brandsDecomposeComponent) {
        val query = (model as? BrandsDecomposeComponent.Model.Loaded)
            ?.query
            .orEmpty()
        mutableStateOf(query.isNotBlank())
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedContent(
                targetState = isQueryShown,
                modifier = modifier,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            ) { localIsQueryShown ->
                if (localIsQueryShown) {
                    val query = (model as? BrandsDecomposeComponent.Model.Loaded)?.query.orEmpty()
                    ComposableSearchBar(
                        text = query,
                        hint = stringResource(BrandsR.string.rcb_search_hint),
                        onClear = {
                            isQueryShown = false
                            brandsDecomposeComponent.onQueryChanged("")
                        },
                        onChangeText = brandsDecomposeComponent::onQueryChanged,
                        onBack = brandsDecomposeComponent::onBackClick
                    )
                } else {
                    SharedTopBar(
                        title = stringResource(BrandsR.string.brands_title),
                        subtitle = stringResource(BrandsR.string.rcb_step_2),
                        onBackClick = brandsDecomposeComponent::onBackClick,
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 14.dp)
                                    .size(24.dp)
                                    .clickableRipple(onClick = { isQueryShown = !isQueryShown }),
                                painter = painterResource(DesignSystem.drawable.ic_search),
                                contentDescription = null,
                                tint = LocalPalletV2.current.icon.blackAndWhite.default
                            )
                        }
                    )
                }
            }
        }
    ) { scaffoldPaddings ->
        Crossfade(targetState = model) { model ->
            when (model) {
                is BrandsDecomposeComponent.Model.Error -> {
                    errorsRenderer.ComposableThrowableError(
                        throwable = model.throwable,
                        onRetry = brandsDecomposeComponent::tryLoad,
                        fapErrorSize = FapErrorSize.FULLSCREEN,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is BrandsDecomposeComponent.Model.Loaded -> {
                    BrandsLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onBrandClick = brandsDecomposeComponent::onBrandClick,
                        onBrandLongClick = brandsDecomposeComponent::onBrandLongClick
                    )
                }

                BrandsDecomposeComponent.Model.Loading -> {
                    BrandsLoadingComposable()
                }
            }
        }
    }
}
