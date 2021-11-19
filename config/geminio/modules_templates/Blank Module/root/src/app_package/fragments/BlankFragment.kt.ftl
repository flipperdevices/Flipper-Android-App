package ${packageName}.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import ${packageName}.composable.ComposableArchive

class ${__formattedModuleName}Fragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        Composable${__formattedModuleName}()
    }
}
