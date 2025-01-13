package com.flipperdevices.bridge.connection.screens.search

import android.R
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
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalTypography
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import flipperapp.components.bridge.connection.sample.shared.generated.resources.Res
import flipperapp.components.bridge.connection.sample.shared.generated.resources.connection_search_title
import me.gulya.anvil.assisted.ContributesAssistedFactory
import org.jetbrains.compose.resources.stringResource
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, ConnectionSearchDecomposeComponent.Factory::class)
class ConnectionSearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val searchViewModelProvider: Provider<ConnectionSearchViewModel>
) : ConnectionSearchDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
        val searchViewModel = viewModelWithFactory(key = null) {
            searchViewModelProvider.get()
        }
        val devices by searchViewModel.getDevicesFlow().collectAsState()

        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(Res.string.connection_search_title),
                style = LocalTypography.current.titleB24
            )
            LazyColumn {
                items(
                    devices,
                    key = { (device, _) -> device.address }
                ) { searchItem ->
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            text = searchItem.device.name ?: searchItem.device.address
                        )

                        Icon(
                            modifier = Modifier
                                .clickableRipple { searchViewModel.onDeviceClick(searchItem) }
                                .padding(16.dp)
                                .size(24.dp),
                            painter = painterResourceByKey(
                                if (searchItem.savedDeviceModel == null) {
                                    R.drawable.ic_menu_add
                                } else {
                                    R.drawable.ic_menu_delete
                                }
                            ),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
