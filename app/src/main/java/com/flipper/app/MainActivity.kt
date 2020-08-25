package com.flipper.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.flipper.app.fragment.HomeFragment
import com.flipper.app.fragment.MarketFragment
import com.flipper.app.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    initView()
  }

  private fun initView() {
    setContentView(R.layout.activity_main)
    setInitialFragment()
    setBottomListener()
  }

  private fun setInitialFragment() {
    main_bottom_navigation.selectedItemId = R.id.bottom_home
    val homeFragment = HomeFragment.newInstance("", "")
    replaceFragment(homeFragment)
  }

  private fun setBottomListener() {
    main_bottom_navigation.setOnNavigationItemSelectedListener { item ->
      // todo perhaps fragments should be provided by DI?
      when (item.itemId) {
        R.id.bottom_home -> {
          replaceFragment(HomeFragment.newInstance("", ""))
          true
        }
        R.id.bottom_market -> {
          replaceFragment(MarketFragment.newInstance("", ""))
          true
        }
        R.id.bottom_settings -> {
          replaceFragment(SettingsFragment())
          true
        }
        else -> false
      }
    }
  }

  private fun replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
      .replace(R.id.content_feed_cl, fragment)
      .commit()
  }
}