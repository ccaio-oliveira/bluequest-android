package br.com.ctech.bluequest.data.session

import br.com.ctech.bluequest.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SessionState {
    data object Loading : SessionState
    data class SignedOut(val expired: Boolean = false) : SessionState
    data class SignedIn(val user: User?) : SessionState
}

@Singleton
class SessionManager @Inject constructor(
    private val tokenStore: TokenStore,
) {
    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    @Volatile
    var token: String? = null
        private set

    suspend fun restore() {
        token = tokenStore.read()
        _state.value = if (token != null) SessionState.SignedIn(user = null) else SessionState.SignedOut()
    }

    suspend fun start(token: String, user: User) {
        tokenStore.save(token)
        this.token = token
        _state.value = SessionState.SignedIn(user)
    }

    fun update(user: User) {
        _state.update { if (it is SessionState.SignedIn) SessionState.SignedIn(user) else it }
    }

    suspend fun end(expired: Boolean = false) {
        tokenStore.clear()
        token = null
        _state.value = SessionState.SignedOut(expired)
    }
}