package ${packageName}

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ${componentName}DecomposeComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext
) : ComponentContext by componentContext, DecomposeComponent {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        TODO()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ${componentName}DecomposeComponent
    }
}