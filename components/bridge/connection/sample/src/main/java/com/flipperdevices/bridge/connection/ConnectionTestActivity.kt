package com.flipperdevices.bridge.connection

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.flipperdevices.bridge.connection.di.AppComponent
import com.flipperdevices.bridge.connection.screens.ConnectionRootDecomposeComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.FlipperTheme
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel
import javax.inject.Inject
import javax.inject.Provider

class ConnectionTestActivity : AppCompatActivity() {
    @Inject
    lateinit var themeViewModelProvider: Provider<ThemeViewModel>

    @Inject
    lateinit var rootComponentFactory: ConnectionRootDecomposeComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<AppComponent>().inject(this)

        val root = rootComponentFactory(
            componentContext = defaultComponentContext(),
        )

        setContent {
            FlipperTheme(
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LocalPallet.current.background)
                    ) {
                        root.Render()
                    }
                },
                themeViewModel = root.viewModelWithFactory(key = null) {
                    themeViewModelProvider.get()
                }
            )
        }
    }
}
