package com.lain.soft.claramobilechallenge.di

import com.lain.soft.claramobilechallenge.data.remote.DiscogsApi
import com.lain.soft.claramobilechallenge.data.remote.interceptor.AuthInterceptor
import com.lain.soft.claramobilechallenge.data.remote.interceptor.ErrorInterceptor
import com.lain.soft.claramobilechallenge.util.Constants.DISCOGS_API_BASE_URL
import com.lain.soft.claramobilechallenge.util.Constants.CONNECTION_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideDiscogsApi(
        baseUrl: String,
        okHttpClient: OkHttpClient,
    ): DiscogsApi =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiscogsApi::class.java)

    @Provides
    @Singleton
    fun provideBaseUrl(): String = DISCOGS_API_BASE_URL

    @Provides
    @Singleton
    fun provideHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(CONNECTION_TIMEOUT, SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, SECONDS)
            .writeTimeout(CONNECTION_TIMEOUT, SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideBasicLevelHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
}
