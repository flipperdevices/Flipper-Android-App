package com.flipper.bottombar.api

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.flipper.bottombar.main.BottomNavigationActivity
import com.flipper.core.api.BottomNavigationActivityApi
import com.flipper.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class BottomNavigationActivityApiImpl @Inject constructor(private val context: Context) :
    BottomNavigationActivityApi {
    override fun openBottomNavigationScreen() {
        context.startActivity(
            Intent(context, BottomNavigationActivity::class.java).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP or
                    FLAG_ACTIVITY_CLEAR_TOP or
                    FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}
