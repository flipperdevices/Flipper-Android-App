package com.flipperdevices.nfceditor.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.RouterProvider
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.nfceditor.sample.databinding.NfcEditorActivityBinding
import com.flipperdevices.nfceditor.sample.di.NfcEditorComponent
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class NfcEditorActivity : AppCompatActivity(), RouterProvider {

    @Inject
    lateinit var cicerone: CiceroneGlobal

    @Inject
    lateinit var nfcEditorApi: NfcEditorApi

    override val router: Router
        get() = cicerone.getRouter()

    private val navigator: Navigator = AppNavigator(this, R.id.content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<NfcEditorComponent>().inject(this)

        val binding = NfcEditorActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val flipperKey = resources.openRawResource(R.raw.mf_4k_part).use {
            FlipperKey(
                path = FlipperKeyPath("test", "test.nfc"),
                keyContent = FlipperKeyContent.RawData(it.readBytes()),
                synchronized = true
            )
        }

        router.newRootScreen(nfcEditorApi.getNfcEditorScreen(flipperKey))
    }

    override fun onResume() {
        super.onResume()
        cicerone.getNavigationHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigationHolder().removeNavigator()
        super.onPause()
    }
}
