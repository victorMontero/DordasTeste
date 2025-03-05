package com.tps.challenge.dagger

import com.tps.challenge.database.repository.StoreFeedRepository
import com.tps.challenge.network.TPSCoroutineService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providesStoreFeedRepository(service: TPSCoroutineService): StoreFeedRepository {
        return StoreFeedRepository(service)
    }
}