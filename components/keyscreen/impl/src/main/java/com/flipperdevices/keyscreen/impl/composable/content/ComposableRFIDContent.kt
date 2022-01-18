package com.flipperdevices.keyscreen.impl.composable.content

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.impl.R

@Composable
fun ComposableRFIDContent(rfid: FlipperKeyParsed.RFID) {
    ComposableKeyContent {
        Text(
            text = FlipperFileType.RFID.humanReadableName,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = colorResource(R.color.keyscreen_text_gray)
        )

        val keyType = rfid.keyType
        if (keyType != null) {
            Text(
                text = "Key type: $keyType",
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = colorResource(R.color.keyscreen_text_gray)
            )
        }

        val data = rfid.data
        if (data != null) {
            Text(
                text = "Data: $data",
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = colorResource(R.color.keyscreen_text_gray)
            )
        }
    }
}
