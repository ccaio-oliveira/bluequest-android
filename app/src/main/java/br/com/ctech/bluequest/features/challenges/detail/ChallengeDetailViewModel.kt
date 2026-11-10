package br.com.ctech.bluequest.features.challenges.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.ChallengeDetail
import br.com.ctech.bluequest.domain.models.ChallengeFeedItem
import br.com.ctech.bluequest.domain.models.ChallengeInvite
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
    val feedItems: List<ChallengeFeedItem>? = null,
    val feedCursor: Int? = null,
    val isLoadingFeed: Boolean = false,
    val feedError: String? = null,
    val inviteSheet: InviteSheetState? = null,
)

data class InviteSheetState(
    val invite: ChallengeInvite? = null,
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
    private var feedJob: Job? = null

    fun load() {
        fetch(isRefresh = false)
    }

    fun refresh() {
        fetch(isRefresh = true)

        val state = _uiState.value
        if (state.feedItems != null || state.feedError != null) {
            loadFeed(reset = true)
        }
    }

    fun onFeedVisible() {
        if (_uiState.value.feedItems == null) {
            loadFeed(reset = true)
        }
    }

    fun loadMoreFeed() {
        loadFeed(reset = false)
    }

    fun onOpenInvite() {
        _uiState.update { it.copy(inviteSheet = InviteSheetState()) }

        viewModelScope.launch {
            try {
                val invite = repository.invite(challengeId)
                _uiState.update { it.copy(inviteSheet = it.inviteSheet?.copy(invite = invite))}
            } catch (e: ApiError) {
                _uiState.update { it.copy(inviteSheet = it.inviteSheet?.copy(errorMessage = e.message)) }
            }
        }
    }

    fun onDismissInvite() {
        _uiState.update { it.copy(inviteSheet = null) }
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

    private fun loadFeed(reset: Boolean) {
        if (feedJob?.isActive == true) return

        val before = if (reset) null else _uiState.value.feedCursor
        _uiState.update { it.copy(isLoadingFeed = true, feedError = null) }

        feedJob = viewModelScope.launch {
            try {
                val page = repository.feed(challengeId, before)

                _uiState.update {
                    it.copy(
                        feedItems = if (reset) page.items else it.feedItems.orEmpty() + page.items,
                        feedCursor = page.nextBefore,
                        isLoadingFeed = false,
                    )
                }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isLoadingFeed = false, feedError = e.message) }
            }
        }
    }
}