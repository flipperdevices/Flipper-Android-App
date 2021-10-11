package com.flipperdevices.pair.impl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.delegates.OnBackPressListener
import com.flipperdevices.pair.api.PairScreenArgument
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.navigation.machine.PairScreenStateDispatcher
import com.flipperdevices.pair.impl.navigation.storage.PairStateStorage
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class PairScreenActivity : FragmentActivity() {
    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    @Inject
    lateinit var pairStateStorage: PairStateStorage

    private val navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair)
        ComponentHolder.component<PairComponent>().inject(this)
        var initialState = pairStateStorage.getSavedPairState()

        val args = getScreenArguments()
        if (args.contains(PairScreenArgument.RECONNECT_DEVICE)) {
            initialState = initialState.copy(devicePaired = false)
        }

        if (savedInstanceState == null) {
            stateDispatcher.invalidate(initialState)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if ((fragment as? OnBackPressListener)?.onBackPressed() == true) {
            return
        } else {
            stateDispatcher.back()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getScreenArguments(): Array<PairScreenArgument> {
        val args = intent.getSerializableExtra(EXTRA_ARGS) as? Array<*> ?: return emptyArray()
        return if (args.isArrayOf<PairScreenArgument>()) {
            args as Array<PairScreenArgument>
        } else emptyArray()
    }

    companion object {
        private const val EXTRA_ARGS = "pair_args"

        fun getLaunchIntent(context: Context, vararg args: PairScreenArgument): Intent {
            val intent = Intent(context, PairScreenActivity::class.java)
            intent.putExtra(EXTRA_ARGS, args)
            return intent
        }
    }
}
