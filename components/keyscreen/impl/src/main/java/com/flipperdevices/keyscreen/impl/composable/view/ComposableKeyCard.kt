package com.flipperdevices.keyscreen.impl.composable.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.dao.api.R as DaoR
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.composable.painterResourceByKey
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.view.content.ComposableIButtonContent
import com.flipperdevices.keyscreen.impl.composable.view.content.ComposableInfraredContent
import com.flipperdevices.keyscreen.impl.composable.view.content.ComposableNFCContent
import com.flipperdevices.keyscreen.impl.composable.view.content.ComposableRFIDContent
import com.flipperdevices.keyscreen.impl.composable.view.content.ComposableSubGhzContent

@Composable
fun ComposableKeyCard(parsedKey: FlipperKeyParsed) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 24.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 18.dp)) {
            ComposableKeyIcon(
                modifier = Modifier.padding(horizontal = 18.dp),
                parsedKey = parsedKey
            )
            Text(
                modifier = Modifier.padding(
                    top = 18.dp,
                    bottom = 12.dp,
                    start = 18.dp,
                    end = 18.dp
                ),
                text = parsedKey.keyName,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp
            )
            val notes = parsedKey.notes
            if (notes != null) {
                Text(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    text = notes,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            } else {
                Text(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    text = stringResource(R.string.keyscreen_card_note_empty),
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = colorResource(R.color.keyscreen_text_gray)
                )
            }
            ComposableKeyContent(parsedKey)
        }
    }
}

@Composable
private fun ComposableKeyIcon(parsedKey: FlipperKeyParsed, modifier: Modifier = Modifier) {
    val fileTypeColor = parsedKey.fileType?.color ?: DaoR.color.fileformat_color_unknown

    Box(
        modifier = modifier
            .background(
                colorResource(fileTypeColor),
                RoundedCornerShape(6.dp)
            )
            .size(46.dp)
    ) {
        val fileTypeIcon = parsedKey.fileType?.icon ?: DaoR.drawable.ic_fileformat_unknown
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            painter = painterResourceByKey(fileTypeIcon),
            contentDescription = parsedKey.fileType?.humanReadableName
                ?: stringResource(DaoR.string.fileformat_unknown)
        )
    }
}

@Composable
private fun ComposableKeyContent(keyParsed: FlipperKeyParsed) {
    when (keyParsed) {
        is FlipperKeyParsed.RFID -> ComposableRFIDContent(keyParsed)
        is FlipperKeyParsed.IButton -> ComposableIButtonContent(keyParsed)
        is FlipperKeyParsed.Infrared -> ComposableInfraredContent(keyParsed)
        is FlipperKeyParsed.NFC -> ComposableNFCContent(keyParsed)
        is FlipperKeyParsed.SubGhz -> ComposableSubGhzContent(keyParsed)
        is FlipperKeyParsed.Unrecognized -> return
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("UnusedPrivateMember")
private fun ComposableKeyCardPreview() {
    val parsedKey = FlipperKeyParsed.RFID(
        keyName = "Test_key",
        data = "DC 69 66 0F 12",
        keyType = "EM4100",
        notes = null
    )
    ComposableKeyCard(parsedKey)
}
