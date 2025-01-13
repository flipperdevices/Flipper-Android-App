package com.flipperdevices.bridge.connection.di

@Singleton
@MergeComponent(AppGraph::class)
interface AndroidAppComponent : AppComponent {
    @MergeComponent.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance application: Application,
            @BindsInstance applicationParams: ApplicationParams
        ): AndroidAppComponent
    }
}
