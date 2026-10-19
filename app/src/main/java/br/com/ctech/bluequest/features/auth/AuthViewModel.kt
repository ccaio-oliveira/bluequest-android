package br.com.ctech.bluequest.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.auth.AuthRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.data.session.SessionManager
import br.com.ctech.bluequest.data.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthMode(val actionTitle: String) {
    SignIn("Entrar"),
    SignUp("Criar conta")
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.SignIn,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.state.collect { state ->
                if (state is SessionState.SignedOut && state.expired) {
                    _uiState.update { it.copy(errorMessage = "Sua sessão expirou. Entre novamente.") }
                }
            }
        }
    }

    fun onModeChange(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                when (state.mode) {
                    AuthMode.SignIn -> authRepository.login(state.email, state.password)
                    AuthMode.SignUp -> authRepository.register(state.name, state.email, state.password)
                }

                _uiState.value = AuthUiState()
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}