package com.flipperdevices.core.ui.theme.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.theme.di.ThemeComponent
import javax.inject.Inject

class ThemeViewModel : ViewModel() {
    @Inject
    lateinit var dataStoreSettings: DataStore<Settings>

//    init {
//        ComponentHolder.component<ThemeComponent>().inject(this)
//    }
}
