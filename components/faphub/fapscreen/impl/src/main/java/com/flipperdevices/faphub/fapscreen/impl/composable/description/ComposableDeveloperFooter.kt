package com.flipperdevices.faphub.fapscreen.impl.composable.description

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapDeveloperInformation
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ColumnScope.ComposableDeveloperFooter(
    developerInformation: FapDeveloperInformation?,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(bottom = 8.dp, top = 32.dp),
        text = stringResource(R.string.fapscreen_developer_title),
        style = LocalTypography.current.titleB18,
        color = LocalPallet.current.text100
    )
    val githubLinkModifier = Modifier.padding(top = 16.dp)

    ComposableGithubLink(
        modifier = if (developerInformation == null) {
            githubLinkModifier.placeholderConnecting()
        } else {
            githubLinkModifier
        },
        textId = R.string.fapscreen_developer_github,
        url = developerInformation?.githubRepositoryLink
    )

    ComposableGithubLink(
        modifier = if (developerInformation == null) {
            githubLinkModifier.placeholderConnecting()
        } else {
            githubLinkModifier
        },
        textId = R.string.fapscreen_developer_manifest,
        url = developerInformation?.manifestRepositoryLink
    )
}

@Composable
private fun ComposableGithubLink(
    @StringRes textId: Int,
    url: String?,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (url != null) {
                    uriHandler.openUri(url)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = stringResource(textId)
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_github),
            contentDescription = text
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = text,
            style = LocalTypography.current.bodyR14.copy(
                textDecoration = TextDecoration.Underline
            ),
            color = LocalPallet.current.text100
        )
    }
}
