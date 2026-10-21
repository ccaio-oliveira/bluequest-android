package br.com.ctech.bluequest.features.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQChip
import br.com.ctech.bluequest.designsystem.components.BQEmptyState
import br.com.ctech.bluequest.designsystem.components.BQOverline
import br.com.ctech.bluequest.designsystem.components.ChallengeCard


@Composable
fun ChallengesRoute(
    onOpenChallenge: (Int) -> Unit,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose {  }
    }

    ChallengesScreen(
        state = state,
        onFilterChange = viewModel::onFilterChange,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::load,
        onOpenChallenge = onOpenChallenge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    state: ChallengesUiState,
    onFilterChange: (ChallengeFilter) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onOpenChallenge: (Int) -> Unit,
) {
    val challenges = state.challenges

    when {
        challenges == null && state.errorMessage != null -> ErrorState(message = state.errorMessage, onRetry = onRetry)

        challenges == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BQColors.text2)
        }

        challenges.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BQEmptyState(
                icon = R.drawable.ic_flag_fill,
                title = "Nenhum desafio ainda",
                message = "Crie um desafio com tarefas e pontos, convide pessoas e acompanhem o ranking. Convites recebidos entram automaticamente."
            )
        }

        else -> PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = BQSpacing.screenPadding,
                    end = BQSpacing.screenPadding,
                    top = BQSpacing.sp2,
                    bottom = BQSpacing.sp6,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { BQOverline("Seus desafios") }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ChallengeFilter.entries.forEach { filter ->
                            BQChip(
                                title = filter.title,
                                isSelected = filter == state.filter,
                                onClick = { onFilterChange(filter) }
                            )
                        }
                    }
                }

                if (state.visibleChallenges.isEmpty()) {
                    item { NoChallengesInFilter(filter = state.filter) }
                }

                items(state.visibleChallenges, key = { it.id }) { challenge ->
                    ChallengeCard(
                        name = challenge.name,
                        periodText = periodText(challenge.startDate, challenge.endDate),
                        state = challenge.state,
                        day = challenge.currentDay,
                        totalDays = challenge.totalDays,
                        points = challenge.myPoints,
                        rank = challenge.myRank,
                        participantNames = challenge.participantNames,
                        participantsCount = challenge.participantsCount,
                        isHero = challenge.id == state.heroId,
                        onClick = { onOpenChallenge(challenge.id) }
                    )
                }

                if (state.filter == ChallengeFilter.Closed) {
                    item {
                        Text(
                            text = "Desafios encerrados ficam no histórico com resultado final.",
                            modifier = Modifier.fillMaxWidth(),
                            style = BQFont.body(12.sp),
                            color = BQColors.text3,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoChallengesInFilter(filter: ChallengeFilter) {
    val shape = RoundedCornerShape(BQRadius.medium)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BQColors.bg1, shape)
            .border(1.dp, BQColors.stroke1, shape)
            .padding(horizontal = BQSpacing.cardPadding, vertical = BQSpacing.sp5),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp1),
    ) {
        Text(
            text = "Nenhum desafio aqui",
            style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold),
            color = BQColors.text2
        )
        Text(
            text = if (filter == ChallengeFilter.Closed) "Desafios que terminaram vão aparecer aqui." else "Crie um desafio ou entre por convite.",
            style = BQFont.body(BQTypeScale.caption),
            color = BQColors.text3,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = BQSpacing.sp10),
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp4, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, color = BQColors.text2, textAlign = TextAlign.Center)
        BQButton(
            title = "Tentar de novo",
            onClick = onRetry,
            icon = R.drawable.ic_refresh,
            variant = BQButtonVariant.Secondary
        )
    }
}