package com.tps.challenge.di

import com.tps.challenge.data.network.TPSCoroutineService
import com.tps.challenge.data.network.repository.storepository.StoreRepository
import com.tps.challenge.data.network.repository.storepository.StoreRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun providesStoreFeedRepository(service: TPSCoroutineService): StoreRepository {
        return StoreRepositoryImpl(service)
    }
}