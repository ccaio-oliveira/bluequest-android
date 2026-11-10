package br.com.ctech.bluequest.features.challenges.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.InvitePreview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InviteUiState(
    val preview: InvitePreview? = null,
    val isLoading: Boolean = false,
    val isAccepting: Boolean = false,
    val errorMessage: String? = null,
    val acceptedChallengeId: Int? = null,
)

@HiltViewModel(assistedFactory = InviteViewModel.Factory::class)
class InviteViewModel @AssistedInject constructor(
    @Assisted private val code: String,
    private val repository: ChallengeRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(code: String): InviteViewModel
    }

    private val _uiState = MutableStateFlow(InviteUiState())
    val uiState: StateFlow<InviteUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val preview = repository.invitePreview(code)
                _uiState.update { it.copy(preview = preview, isLoading = false) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun accept() {
        if (_uiState.value.isAccepting) return

        _uiState.update { it.copy(isAccepting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val challengeId = repository.acceptInvite(code)
                _uiState.update { it.copy(isAccepting = false, acceptedChallengeId = challengeId) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isAccepting = false, errorMessage = e.message) }
                load()
            }
        }
    }
}