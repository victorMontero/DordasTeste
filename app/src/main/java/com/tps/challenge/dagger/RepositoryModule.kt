package com.tps.challenge.dagger

import com.tps.challenge.network.repository.storepository.StoreRepository
import com.tps.challenge.network.TPSCoroutineService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providesStoreFeedRepository(service: TPSCoroutineService): StoreRepository {
        return StoreRepository(service)
    }
}