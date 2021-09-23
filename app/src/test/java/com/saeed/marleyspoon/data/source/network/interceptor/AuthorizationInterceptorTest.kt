package com.saeed.marleyspoon.data.source.network.interceptor

import android.util.Log
import com.apollographql.apollo3.api.http.HttpBody
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okio.ByteString.Companion.encodeUtf8
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class AuthorizationInterceptorTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var authorizationInterceptor: AuthorizationInterceptor

    val request = spyk(
        HttpRequest(
            method = HttpMethod.Get,
            url = "https://www.test.com",
            headers = emptyList(),
            body = HttpBody("text/plain", "0".encodeUtf8())
        )
    )

    //    val request = mockk<HttpRequest>(relaxed = true)
    val chain = mockk<HttpInterceptorChain>()

    @Before
    fun init() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        Truth.assertThat(this::authorizationInterceptor.isInitialized).isFalse()
        hiltRule.inject()

        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test add header to a request in AuthorizationInterceptor`() {

        // assert that Hilt was able to provide AuthorizationInterceptor
        Truth.assertThat(authorizationInterceptor).isNotNull()

        // assert that request header does not contain Authorization header
        Truth.assertThat(request.headers.find { it.name == "Authorization" }).isNull()

        // return mock response based on Authorization header
        coEvery {
            chain.proceed(any())
        } coAnswers { call ->
           if( (call.invocation.args.firstOrNull() as? HttpRequest)?.headers?.find { it.name == "Authorization" }!=null){
               HttpResponse(200, arrayListOf(), null, "0".encodeUtf8())
           }else{
               HttpResponse(403, arrayListOf(), null, "1".encodeUtf8())
           }
        }

        // call authorizationInterceptor with mock arguments
        runBlockingTest {
            val response = authorizationInterceptor.intercept(request, chain)

            // assert that response status code is 200
            Truth.assertThat(response.statusCode).isEqualTo(200)
        }

        // verify that request proceed
        coVerify(exactly = 1) {
            chain.proceed(any())
        }
    }
}

