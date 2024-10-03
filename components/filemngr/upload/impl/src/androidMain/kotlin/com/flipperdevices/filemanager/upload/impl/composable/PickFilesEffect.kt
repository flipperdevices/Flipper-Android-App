package com.flipperdevices.filemanager.upload.impl.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.coroutines.runBlocking

@Composable
fun PickFilesEffect(
    deepLinkParser: DeepLinkParser,
    onBack: () -> Unit,
    onContentsReady: (List<DeeplinkContent>) -> Unit
) {
    val context = LocalContext.current
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uri ->
        val deeplinkContents = runBlocking {
            uri.map { deepLinkParser.fromUri(context, it) }
                .filterIsInstance<Deeplink.RootLevel.SaveKey.ExternalContent>()
                .mapNotNull { it.content }
        }
        if (deeplinkContents.isEmpty()) onBack.invoke()
        onContentsReady.invoke(deeplinkContents)
    }

    LaunchedEffect(pickFileLauncher) {
        pickFileLauncher.launch("*/*")
    }
}
