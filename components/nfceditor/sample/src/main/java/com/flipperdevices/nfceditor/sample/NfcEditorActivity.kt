package com.flipperdevices.nfceditor.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.defaultComponentContext
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.nfceditor.api.NfcEditorDecomposeComponent
import javax.inject.Inject

class NfcEditorActivity : AppCompatActivity() {

    @Inject
    lateinit var featureEntryFactory: NfcEditorDecomposeComponent.Factory

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NfcEditorApplication.appComponent.inject(this)

        val flipperKey = resources.openRawResource(R.raw.mf_4k_full).use {
            FlipperKey(
                mainFile = FlipperFile(
                    path = FlipperFilePath("test", "test.nfc"),
                    content = FlipperKeyContent.RawData(it.readBytes())
                ),
                synchronized = true,
                deleted = false
            )
        }
        val root = featureEntryFactory(
            componentContext = defaultComponentContext(),
            flipperKeyPath = flipperKey.getKeyPath(),
            onBack = {}
        )

        setContent {
            var applicationIsReady by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = Unit) {
                val isExistKey = simpleKeyApi.getKey(flipperKey.getKeyPath()) == null
                if (isExistKey) {
                    simpleKeyApi.insertKey(flipperKey)
                }
                applicationIsReady = true
            }

            FlipperThemeInternal {
                if (!applicationIsReady) {
                    CircularProgressIndicator()
                } else {
                    root.Render()
                }
            }
        }
    }
}
