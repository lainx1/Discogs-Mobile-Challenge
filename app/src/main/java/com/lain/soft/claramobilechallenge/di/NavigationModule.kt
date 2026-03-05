package com.lain.soft.claramobilechallenge.di

import com.lain.soft.claramobilechallenge.ui.navigation.RouteArgDecoder
import com.lain.soft.claramobilechallenge.ui.navigation.UrlRouteArgDecoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindRouteArgDecoder(impl: UrlRouteArgDecoder): RouteArgDecoder
}
