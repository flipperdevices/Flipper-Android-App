package com.flipperdevices.nfceditor.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.nfceditor.api.NfcEditorFeatureEntry
import com.flipperdevices.nfceditor.sample.di.NfcEditorComponent
import javax.inject.Inject

class NfcEditorActivity : AppCompatActivity() {

    @Inject
    lateinit var featureEntry: NfcEditorFeatureEntry

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<NfcEditorComponent>().inject(this)

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

        setContent {
            var applicationIsReady by remember { mutableStateOf(false) }
            val nfcEditorScreen = featureEntry.getNfcEditorScreen(flipperKey.getKeyPath())
            val navController = rememberNavController()

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
                    NavHost(
                        navController = navController,
                        startDestination = nfcEditorScreen
                    ) {
                        with(featureEntry) {
                            composable(navController)
                        }
                        composable(
                            route = featureEntry.ROUTE.name
                        ) {
                            Button(onClick = {
                                navController.navigate(nfcEditorScreen)
                            }) {
                                Text("Open nfc editor")
                            }
                        }
                    }
                }
            }
        }
    }
}
