package com.portfolio.photocatalog.data.network

import com.portfolio.photocatalog.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", BuildConfig.API_KEY)
            .build()

        return chain.proceed(newRequest)
    }
}