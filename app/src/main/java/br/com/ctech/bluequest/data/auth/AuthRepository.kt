package br.com.ctech.bluequest.data.auth

import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.data.network.apiCall
import br.com.ctech.bluequest.data.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager,
) {
    suspend fun login(email: String, password: String) {
        val response = apiCall { api.login(LoginRequest(email, password)) }
        sessionManager.start(response.token, response.user.toDomain())
    }

    suspend fun register(name: String, email: String, password: String) {
        val response = apiCall { api.register(RegisterRequest(name, email, password)) }
        sessionManager.start(response.token, response.user.toDomain())
    }

    suspend fun refreshCurrentUser() {
        val user = apiCall { api.me() }
        sessionManager.update(user.toDomain())
    }

    suspend fun logout() {
        try {
            apiCall { api.logout() }
        } catch (e: ApiError) {
            // Sair funciona mesmo sem rede: o token local é apagado de qualquer forma.
        }

        sessionManager.end()
    }
}