package com.flipperdevices.pair.impl.composable.tos

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.pair.impl.R
import com.flipperdevices.pair.impl.composable.common.ComposableAgreeButton

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableTOS(
    onAgreeClickListener: () -> Unit = {}
) {
    Scaffold(bottomBar = {
        TOSBottomBar(onAgreeClickListener)
    }) {
        Box(Modifier.padding(it)) {
            TOSContent()
        }
    }
}

@Composable
fun TOSContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pair_tos_welcome),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(R.drawable.ic_welcome),
            contentDescription = stringResource(R.string.pair_tos_pic_welcome)
        )
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = stringResource(R.string.pair_tos_title),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.pair_tos_description),
            fontSize = 14.sp
        )
        TOSPoint(R.string.pair_tos_collect_point_1)
        TOSPoint(R.string.pair_tos_collect_point_2)
        TOSPoint(R.string.pair_tos_collect_point_3)
        TOSPoint(R.string.pair_tos_collect_point_4)
        TOSFooter()
    }
}

@Composable
private fun TOSPoint(@StringRes stringResId: Int) {
    Text(
        text = "• ${stringResource(id = stringResId)}",
        color = Color.Black,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    )
}

private const val STRING_TAG_URL = "URL"

@Composable
private fun TOSFooter() {
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.pair_tos_footer_full_1))
        addLink(
            R.string.pair_tos_footer_user_agreement,
            R.string.pair_tos_footer_user_agreement_link
        )
        append(stringResource(R.string.pair_tos_footer_full_2))
        addLink(
            R.string.pair_tos_footer_privacy_policy,
            R.string.pair_tos_footer_privacy_policy_link
        )
        append(stringResource(R.string.pair_tos_footer_full_3))
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        onClick = { position ->
            val annotation =
                annotatedString.getStringAnnotations(STRING_TAG_URL, position, position)
            annotation.firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }
        },
        style = TextStyle(
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
private fun AnnotatedString.Builder.addLink(
    @StringRes textResId: Int,
    @StringRes linkResId: Int
) {
    pushStringAnnotation(
        tag = STRING_TAG_URL,
        annotation = stringResource(linkResId)
    )
    withStyle(
        style = SpanStyle(
            textDecoration = TextDecoration.Underline,
            color = Color.Blue
        )
    ) {
        append(stringResource(textResId))
    }
    pop()
}

@Composable
fun TOSBottomBar(onAgreeClickListener: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ComposableAgreeButton(stringResource(R.string.pair_tos_button_text), onAgreeClickListener)
    }
}
