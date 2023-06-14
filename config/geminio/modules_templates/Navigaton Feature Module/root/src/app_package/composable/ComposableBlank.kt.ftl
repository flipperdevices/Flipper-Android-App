package ${packageName}.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

@Composable
internal fun Composable${__formattedModuleName}() {

}

@Preview
@Composable
private fun PreviewComposable${__formattedModuleName}() {
    FlipperThemeInternal {
        Composable${__formattedModuleName}()
    }
}
