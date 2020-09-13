package com.flipper.core

import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.flipper.integrationtest.CounterController
import com.flipper.integrationtest.TestActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class BaseControllerTest {

    private lateinit var activityController: ActivityController<TestActivity>
    private lateinit var router: Router

    private fun createActivityController(savedInstanceState: Bundle? = null) {
        activityController = Robolectric.buildActivity(TestActivity::class.java)
        activityController.create().start().postCreate(null).resume()
        val activity = activityController.get()
        router = Conductor.attachRouter(activity, activity.contentView, savedInstanceState)
    }

    @Before
    fun setup() {
        createActivityController(null)
    }

    @Test
    fun `on controller recreation should not recreate presenter`() {
        // Arrange
        val controller = CounterController()
        router.setRoot(RouterTransaction.with(controller))
        val presenter = controller.presenter
        val bundle = Bundle().also(router::saveInstanceState)

        // Act
        router.popCurrentController()
        router.restoreInstanceState(bundle)
        router.rebindIfNeeded()
        val newController = router.backstack.last().controller as CounterController
        val newPresenter = newController.presenter

        // Assert
        assertThat(newController).isNotSameAs(controller)
        assertThat(newPresenter).isSameAs(presenter)
    }
}
