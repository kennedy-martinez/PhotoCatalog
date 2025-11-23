package com.portfolio.photocatalog.data.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class MockDataInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.contains("items")) {

            Thread.sleep(1000)

            val jsonResponse = generateFakeJson()

            return Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(jsonResponse.toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("content-type", "application/json")
                .build()
        }

        return chain.proceed(request)
    }

    private fun generateFakeJson(): String {
        val items = (1..20).joinToString(",") { id ->
            """
            {
                "id": "mock_$id",
                "text": "Mock Photo Description #$id generated locally",
                "url": "https://picsum.photos/id/$id/300/300", 
                "confidence": 0.${80 + (id % 20)}
            }
            """.trimIndent()
        }
        return "[$items]"
    }
}