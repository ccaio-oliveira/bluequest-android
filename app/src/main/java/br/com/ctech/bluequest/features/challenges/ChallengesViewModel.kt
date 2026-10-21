package br.com.ctech.bluequest.features.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.ChallengeState
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ChallengeFilter(val title: String, val state: ChallengeState) {
    InProgress("Em andamento", ChallengeState.InProgress),
    Future("Futuros", ChallengeState.Future),
    Closed("Encerrados", ChallengeState.Closed)
}

data class ChallengesUiState(
    val challenges: List<ChallengeSummary>? = null,
    val filter: ChallengeFilter = ChallengeFilter.InProgress,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleChallenges: List<ChallengeSummary>
        get() = challenges.orEmpty().filter { it.state == filter.state }

    val heroId: Int?
        get() = challenges?.firstOrNull { it.state == ChallengeState.InProgress }?.id
}

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val repository: ChallengeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load() {
        fetch(isRefresh = false)
    }

    fun refresh() {
        fetch(isRefresh = true)
    }

    fun onFilterChange(filter: ChallengeFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    private fun fetch(isRefresh: Boolean) {
        if (loadJob?.isActive == true) return

        val isFirstLoad = _uiState.value.challenges == null
        _uiState.update { it.copy(isLoading = isFirstLoad, isRefreshing = isRefresh, errorMessage = null) }

        loadJob = viewModelScope.launch {
            try {
                val challenges = repository.challenges()
                _uiState.update { it.copy(challenges = challenges, isLoading = false, isRefreshing = false) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.message) }
            }
        }
    }
}