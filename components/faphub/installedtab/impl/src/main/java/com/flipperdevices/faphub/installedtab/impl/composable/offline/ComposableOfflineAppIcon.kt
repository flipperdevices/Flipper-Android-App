package com.flipperdevices.faphub.installedtab.impl.composable.offline

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import com.flipperdevices.core.ui.ktx.image.WhiteToAlphaTransformation
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.installedtab.impl.R
import java.util.Base64

@Composable
@Suppress("ModifierReused")
fun ComposableOfflineAppIcon(
    iconBase64: String?,
    modifier: Modifier = Modifier
) {
    var isFailedLoaded by remember { mutableStateOf(false) }

    var modifierWithClip = modifier
        .clip(RoundedCornerShape(6.dp))

    if (iconBase64 == null || isFailedLoaded) {
        ComposableOfflineAppIconFailed(modifierWithClip)
        return
    }

    var isPlaceholderActive by remember { mutableStateOf(true) }

    modifierWithClip = if (isPlaceholderActive) {
        modifierWithClip.placeholderConnecting()
    } else {
        modifierWithClip.border(1.dp, LocalPallet.current.text16, RoundedCornerShape(6.dp))
    }

    val context = LocalContext.current
    val request = remember(iconBase64) {
        runCatching {
            val decoded = Base64.getDecoder().decode(iconBase64)
            ImageRequest.Builder(context)
                .transformations(WhiteToAlphaTransformation())
                .data(decoded)
                .build()
        }.getOrNull()
    }

    Box(
        modifier = modifierWithClip
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(all = 4.dp)
                .fillMaxSize(),
            model = request,
            contentDescription = null,
            filterQuality = FilterQuality.None,
            colorFilter = ColorFilter.tint(LocalPallet.current.text100),
            contentScale = ContentScale.FillBounds,
            onLoading = { isPlaceholderActive = true },
            onSuccess = { isPlaceholderActive = false },
            onError = {
                isPlaceholderActive = false
                isFailedLoaded = false
            }
        )
    }
}

@Composable
private fun ComposableOfflineAppIconFailed(
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Icon(
            modifier = Modifier
                .padding(all = 4.dp)
                .fillMaxSize(),
            painter = painterResource(R.drawable.ic_unknown),
            contentDescription = null,
            tint = LocalPallet.current.fapHubOnIcon
        )
    }
}
