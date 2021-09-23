package com.saeed.marleyspoon.data.source.network.interceptor

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.api.http.withHeader
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.saeed.marleyspoon.BuildConfig
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(): HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {

        return chain.proceed(
            request.withHeader("Authorization", "Bearer ${BuildConfig.CONTENTFUL_TOKEN_ID}")
        )
    }
}