package com.lain.soft.claramobilechallenge.di

import com.lain.soft.claramobilechallenge.data.repository.ArtistRepositoryImpl
import com.lain.soft.claramobilechallenge.domain.repository.ArtistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindArtistRepository(impl: ArtistRepositoryImpl): ArtistRepository
}
