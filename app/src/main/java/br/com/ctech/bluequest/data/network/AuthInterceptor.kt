package br.com.ctech.bluequest.data.network

import br.com.ctech.bluequest.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.token

        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .apply { if (token != null) header("Authorization", "Bearer $token") }
            .build()

        val response = chain.proceed(request)

        if (response.code == 401 && token != null && token == sessionManager.token) {
            runBlocking { sessionManager.end(expired = true) }
        }

        return response
    }
}