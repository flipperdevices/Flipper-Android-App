package com.flipper.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.app.databinding.ActivityMainBinding
import com.flipper.app.home.ui.HomeController
import com.lionzxy.trex_offline.TRexOfflineActivity

const val ALPHA_VERSION_TEXT = 0.25F

class MainActivity : AppCompatActivity() {
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.INTERNAL) {
            binding.versionName.visibility = View.VISIBLE
            binding.versionName.text = BuildConfig.VERSION_NAME
            binding.versionName.alpha = ALPHA_VERSION_TEXT
            binding.versionName.setOnClickListener {
                TRexOfflineActivity.open(this)
                false
            }
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
