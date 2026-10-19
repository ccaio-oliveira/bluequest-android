package br.com.ctech.bluequest.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.auth.AuthRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.data.session.SessionManager
import br.com.ctech.bluequest.data.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val sessionState: StateFlow<SessionState> = sessionManager.state

    init {
        viewModelScope.launch {
            sessionManager.restore()

            if (sessionManager.state.value is SessionState.SignedIn) {
                try {
                    authRepository.refreshCurrentUser()
                } catch (e: ApiError) {
                    // Sem rede, o app segue com a sessão salva. Um 401 já encerrou a sessão no interceptor.
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}