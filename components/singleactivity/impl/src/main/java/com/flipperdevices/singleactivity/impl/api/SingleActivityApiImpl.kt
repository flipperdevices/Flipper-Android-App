package com.flipperdevices.singleactivity.impl.api

import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.singleactivity.api.SingleActivityApi
import com.flipperdevices.singleactivity.impl.SingleActivity
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class SingleActivityApiImpl @Inject constructor(
    private val context: Context
) : SingleActivityApi {
    override fun open() {
        context.startActivity(
            Intent(context, SingleActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}
