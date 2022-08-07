package com.flipperdevices.bottombar

import androidx.compose.ui.graphics.Color
import com.flipperdevices.bottombar.model.TabState
import org.junit.Assert
import org.junit.Test

class TabStateTest {

    private val animatedTabStateFirst = TabState.Animated(
        selectedIcon = 1,
        notSelectedIcon = 2,
        text = "TabState Animated 1",
        selectedColor = Color(0xFF111111),
        unselectedColor = Color(0xFF222222),
        selectedBackground = 0,
        notSelectedBackground = 0
    )

    private val animatedTabStateSecond = TabState.Animated(
        selectedIcon = 1,
        notSelectedIcon = 2,
        text = "TabState Animated 1",
        selectedColor = Color(0xFF111111),
        unselectedColor = Color(0xFF222222),
        selectedBackground = 0,
        notSelectedBackground = 0
    )

    private val staticTabStateFirst = TabState.Static(
        selectedIcon = 1,
        notSelectedIcon = 2,
        text = "TabState Static 1",
        selectedColor = Color(0xFF111111),
        unselectedColor = Color(0xFF222222)
    )

    private val staticTabStateSecond = TabState.Static(
        selectedIcon = 1,
        notSelectedIcon = 2,
        text = "TabState Static 1",
        selectedColor = Color(0xFF111111),
        unselectedColor = Color(0xFF222222)
    )

    @Test
    fun `Equals object animated tab state`() {
        Assert.assertEquals(animatedTabStateFirst, animatedTabStateSecond)
    }

    @Test
    fun `Equals hashcode from animated tab state`() {
        Assert.assertEquals(animatedTabStateFirst.hashCode(), animatedTabStateSecond.hashCode())
    }

    @Test
    fun `Equals object static tab state`() {
        Assert.assertEquals(staticTabStateFirst, staticTabStateSecond)
    }

    @Test
    fun `Equals hashcode from static tab state`() {
        Assert.assertEquals(staticTabStateFirst.hashCode(), staticTabStateSecond.hashCode())
    }
}
