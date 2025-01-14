package com.flipperdevices.bridge.connection.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import flipperapp.components.bridge.connection.sample.shared.generated.resources.Res
import flipperapp.components.bridge.connection.sample.shared.generated.resources.connection_search_title
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_add_box
import flipperapp.components.bridge.connection.sample.shared.generated.resources.material_ic_delete
import flipperapp.components.core.ui.res.generated.resources.material_ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import javax.inject.Provider
import flipperapp.components.core.ui.res.generated.resources.Res as SharedRes

class ConnectionSearchDecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val searchViewModelProvider: Provider<ConnectionSearchViewModel>
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    override fun Render() {
        val searchViewModel = viewModelWithFactory(key = null) {
            searchViewModelProvider.get()
        }
        val devices by searchViewModel.getDevicesFlow().collectAsState()

        Column {
            Row {
                Icon(
                    modifier = Modifier
                        .clickableRipple { onBack() }
                        .padding(16.dp)
                        .size(24.dp),
                    painter = painterResource(SharedRes.drawable.material_ic_close),
                    contentDescription = null,
                    tint = LocalPallet.current.text100
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.connection_search_title),
                    style = LocalTypography.current.titleB24,
                    color = LocalPallet.current.text100
                )
            }
            LazyColumn {
                items(
                    devices,
                    key = { device -> device.address }
                ) { searchItem ->
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            text = searchItem.deviceModel.humanReadableName,
                            color = LocalPallet.current.text100
                        )

                        Icon(
                            modifier = Modifier
                                .clickableRipple { searchViewModel.onDeviceClick(searchItem) }
                                .padding(16.dp)
                                .size(24.dp),
                            painter = painterResource(
                                if (searchItem.isAdded) {
                                    Res.drawable.material_ic_delete
                                } else {
                                    Res.drawable.material_ic_add_box
                                }
                            ),
                            contentDescription = null,
                            tint = LocalPallet.current.text100
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): ConnectionSearchDecomposeComponent
    }
}
