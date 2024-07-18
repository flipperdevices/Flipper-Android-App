package com.flipperdevices.ifrmvp.api.infrared.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import javax.inject.Qualifier

@Module
@ContributesTo(AppGraph::class)
class InfraredKtorfitModule {
    @Qualifier
    annotation class InfraredRemoteScope

    @Provides
    @Reusable
    @InfraredRemoteScope
    fun provideRetrofit(
        httpClient: HttpClient,
    ): Ktorfit {
        val url = "$HOST/"
        return Ktorfit.Builder()
            .baseUrl(url)
            .httpClient(httpClient)
            .build()
    }

    @Provides
    @Reusable
    fun provideApplicationApi(@InfraredRemoteScope ktorfit: Ktorfit): InfraredBackendApi {
        return ktorfit.create()
    }

    companion object {
        // TODO CHANGE HOST AND AndroidManifest.xml usesCleartextTraffic AFTER https://github.com/flipperdevices/IRDB-Backend DEPLOY
        private const val HOST = "http://192.168.0.102:8080"
//        private const val HOST = "https://infrared.dev.flipp.dev"
    }
}
