package com.tps.challenge.di

import com.tps.challenge.TCApplication
import com.tps.challenge.ui.features.storefeed.StoreFeedFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: TCApplication)
    fun inject(storeFeedFragment: StoreFeedFragment)
}
