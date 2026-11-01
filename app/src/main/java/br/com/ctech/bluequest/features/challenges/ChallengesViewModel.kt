package br.com.ctech.bluequest.features.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.ChallengeState
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import br.com.ctech.bluequest.domain.models.OccurrenceState
import br.com.ctech.bluequest.domain.models.TodayOccurrence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val occurrences: List<TodayOccurrence> = emptyList(),
    val filter: ChallengeFilter = ChallengeFilter.InProgress,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val sendingTaskIds: Set<Int> = emptySet(),
    val pointsPop: Int? = null,
    val toastMessage: String? = null,
) {
    val visibleChallenges: List<ChallengeSummary>
        get() = challenges.orEmpty().filter { it.state == filter.state }

    val heroId: Int?
        get() = challenges?.firstOrNull { it.state == ChallengeState.InProgress }?.id

    val todayPoints: Int
        get() = occurrences.sumOf { it.pointsAwarded ?: 0 }

    val completedCount: Int
        get() = occurrences.count { it.state == OccurrenceState.Completed }

    val doableCount: Int
        get() = occurrences.count { it.state != OccurrenceState.Future && !it.isOptional }
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

    fun completeTask(occurrence: TodayOccurrence) {
        if (occurrence.taskId in _uiState.value.sendingTaskIds) return

        _uiState.update { it.copy(sendingTaskIds = it.sendingTaskIds + occurrence.taskId) }

        viewModelScope.launch {
            try {
                repository.completeTask(occurrence.taskId, occurrence.occurrenceDate)
                _uiState.update { it.markingCompleted(occurrence).copy(pointsPop = occurrence.points) }
            } catch (e: ApiError) {
                val alreadyCompleted = e is ApiError.DomainRule && e.reason == "occurrence_not_available" && e.state == "completed"

                _uiState.update {
                    if (alreadyCompleted) it.markingCompleted(occurrence)
                    else it.copy(toastMessage = "${occurrence.name}: ${e.message}")
                }
            }

            _uiState.update { it.copy(sendingTaskIds = it.sendingTaskIds - occurrence.taskId) }

            loadJob?.cancel()
            startLoading()
        }
    }

    fun onPointsPopFinished() {
        _uiState.update { it.copy(pointsPop = null) }
    }

    fun onToastFinished() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun fetch(isRefresh: Boolean) {
        if (loadJob?.isActive == true) return

        val isFirstLoad = _uiState.value.challenges == null
        _uiState.update { it.copy(isLoading = isFirstLoad, isRefreshing = isRefresh, errorMessage = null) }

        startLoading()
    }

    private fun startLoading() {
        loadJob = viewModelScope.launch {
            try {
                val (occurrences, challenges) = coroutineScope {
                    val today = async { repository.today() }
                    val challenges = async { repository.challenges() }

                    today.await() to challenges.await()
                }

                _uiState.update { it.copy(occurrences = occurrences, challenges = challenges, isLoading = false, isRefreshing = false) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.message) }
            }
        }
    }

    private fun ChallengesUiState.markingCompleted(occurrence: TodayOccurrence) = copy(
        occurrences = occurrences.map {
            if (it.taskId == occurrence.taskId) {
                it.copy(
                    state = OccurrenceState.Completed,
                    pointsAwarded = it.points,
                    weekly = it.weekly?.let { weekly -> weekly.copy(done = weekly.done + 1) }
                )
            } else {
                it
            }
        }
    )
}