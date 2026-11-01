package br.com.ctech.bluequest.features.challenges.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.ChallengeDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengeDetailUiState(
    val detail: ChallengeDetail? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel(assistedFactory = ChallengeDetailViewModel.Factory::class)
class ChallengeDetailViewModel @AssistedInject constructor(
    @Assisted private val challengeId: Int,
    private val repository: ChallengeRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(challengeId: Int): ChallengeDetailViewModel
    }

    private val _uiState = MutableStateFlow(ChallengeDetailUiState())
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load() {
        fetch(isRefresh = false)
    }

    fun refresh() {
        fetch(isRefresh = true)
    }

    private fun fetch(isRefresh: Boolean) {
        if (loadJob?.isActive == true) return

        _uiState.update { it.copy(isRefreshing = isRefresh, errorMessage = null) }

        loadJob = viewModelScope.launch {
            try {
                val detail = repository.challenge(challengeId)
                _uiState.update { it.copy(detail = detail, isRefreshing = false) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isRefreshing = false, errorMessage = e.message) }
            }
        }
    }
}