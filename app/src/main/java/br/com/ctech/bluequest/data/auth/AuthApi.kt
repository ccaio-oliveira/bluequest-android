package br.com.ctech.bluequest.data.auth

import br.com.ctech.bluequest.domain.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("me")
    suspend fun me(): UserDto

    @POST("logout")
    suspend fun logout()
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)

@Serializable
data class AuthResponse(val user: UserDto, val token: String)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("has_password") val hasPassword: Boolean,
) {
    fun toDomain() = User(id = id, name = name, email = email, avatarUrl = avatarUrl, hasPassword = hasPassword)
}