package ${packageName}.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.fragment.ComposeFragment
import ${packageName}.composable.Composable${__formattedModuleName}

class ${__formattedModuleName}Fragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        Composable${__formattedModuleName}()
    }
}
