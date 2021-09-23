package com.saeed.marleyspoon.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.ApolloClientAwarenessInterceptor
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.saeed.marleyspoon.BuildConfig
import com.saeed.marleyspoon.data.source.network.interceptor.AuthorizationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(): HttpInterceptor {
        return AuthorizationInterceptor()
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .build()

    @Provides
    @Singleton
    @ContentFulGraphQL
    fun provideNetwork(
        okHttpClient: OkHttpClient,
        authorizationInterceptor: HttpInterceptor
    ): ApolloClient {
        return ApolloClient(
            networkTransport = HttpNetworkTransport(
                serverUrl = BuildConfig.CONTENTFUL_SERVER_URL,
                engine = DefaultHttpEngine(okHttpClient),
                interceptors = listOf(
                    authorizationInterceptor,
                    ApolloClientAwarenessInterceptor(
                        BuildConfig.APPLICATION_ID,
                        BuildConfig.VERSION_NAME
                    )
                )
            )
        )

    }


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ContentFulGraphQL


}