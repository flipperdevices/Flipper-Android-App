package com.flipper.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.app.databinding.ActivityMainBinding
import com.flipper.app.home.HomeController

class MainActivity : AppCompatActivity() {
  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    if (BuildConfig.DEBUG) {
      binding.versionName.visibility = View.VISIBLE
      binding.versionName.text = BuildConfig.VERSION_NAME
      binding.versionName.alpha = 0.1F
    }
    initializeConductor(binding.container, savedInstanceState)
  }

  private fun initializeConductor(container: ViewGroup, savedInstanceState: Bundle?) {
    router = Conductor.attachRouter(this, container, savedInstanceState)
    if (!router.hasRootController()) {
      router.setRoot(RouterTransaction.with(HomeController()))
    }
  }

  override fun onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed()
    }
  }
}
