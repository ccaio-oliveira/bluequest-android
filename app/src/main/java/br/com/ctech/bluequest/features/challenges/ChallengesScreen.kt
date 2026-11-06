package br.com.ctech.bluequest.features.challenges

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import br.com.ctech.bluequest.designsystem.components.BQButtonSize
import br.com.ctech.bluequest.designsystem.components.BQChip
import br.com.ctech.bluequest.designsystem.components.BQEmptyState
import br.com.ctech.bluequest.designsystem.components.BQErrorState
import br.com.ctech.bluequest.designsystem.components.BQErrorToast
import br.com.ctech.bluequest.designsystem.components.BQOverline
import br.com.ctech.bluequest.designsystem.components.BQPointsPill
import br.com.ctech.bluequest.designsystem.components.BQProgressRing
import br.com.ctech.bluequest.designsystem.components.ChallengeCard
import br.com.ctech.bluequest.designsystem.components.TaskCard
import br.com.ctech.bluequest.domain.models.TodayOccurrence
import kotlinx.coroutines.delay
import java.time.LocalDate


@Composable
fun ChallengesRoute(
    onOpenChallenge: (Int) -> Unit,
    onCreateChallenge: () -> Unit,
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
        onOpenChallenge = onOpenChallenge,
        onCreateChallenge = onCreateChallenge,
        onCompleteTask = viewModel::completeTask,
        onPointsPopFinished = viewModel::onPointsPopFinished,
        onToastFinished = viewModel::onToastFinished
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
    onCreateChallenge: () -> Unit,
    onCompleteTask: (TodayOccurrence, Uri?) -> Unit,
    onPointsPopFinished: () -> Unit,
    onToastFinished: () -> Unit,
) {
    val challenges = state.challenges
    var photoTaskId by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(Modifier.fillMaxSize()) {
        when {
            challenges == null && state.errorMessage != null -> BQErrorState(message = state.errorMessage, onRetry = onRetry)

            challenges == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BQColors.text2)
            }

            challenges.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BQEmptyState(
                    icon = R.drawable.ic_flag_fill,
                    title = "Nenhum desafio ainda",
                    message = "Crie um desafio com tarefas e pontos, convide pessoas e acompanhem o ranking. Convites recebidos entram automaticamente.",
                    action = {
                        BQButton(title = "Criar desafio", onClick = onCreateChallenge, icon = R.drawable.ic_add)
                    }
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
                    item {
                        TodayHeader(
                            date = LocalDate.now(),
                            points = state.todayPoints,
                            completedCount = state.completedCount,
                            doableCount = state.doableCount,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    item {
                        TodayTasks(
                            occurrences = state.occurrences,
                            sendingTaskIds = state.sendingTaskIds,
                            onCompleteTask = { occurrence ->
                                if (occurrence.hasPhoto) photoTaskId = occurrence.taskId
                                else onCompleteTask(occurrence, null)
                            },
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

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
                        item {
                            EmptyCard(
                                title = "Nenhum desafio aqui",
                                message = if (state.filter == ChallengeFilter.Closed) "Desafios que terminaram vão aparecer aqui." else "Crie um desafio ou entre por convite."
                            )
                        }
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

                    item {
                        BQButton(
                            title = "Criar desafio",
                            onClick = onCreateChallenge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            icon = R.drawable.ic_add,
                            size = BQButtonSize.Large
                        )
                    }
                }
            }
        }

        state.occurrences.firstOrNull { it.taskId == photoTaskId }?.let { occurrence ->
            CompletionPhotoSheet(
                taskName = occurrence.name,
                points = occurrence.points,
                onPhotoSelected = { photo ->
                    photoTaskId = null
                    onCompleteTask(occurrence, photo)
                },
                onDismiss = { photoTaskId = null}
            )
        }

        state.pointsPop?.let { points ->
            PointsPop(
                points = points,
                onFinished = onPointsPopFinished,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp)
            )
        }

        state.toastMessage?.let { message ->
            ErrorToast(
                message = message,
                onFinished = onToastFinished,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        top = BQSpacing.sp3,
                        start = BQSpacing.screenPadding,
                        end = BQSpacing.screenPadding
                    )
            )
        }
    }
}

@Composable
private fun TodayHeader(
    date: LocalDate,
    points: Int,
    completedCount: Int,
    doableCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(BQSpacing.sp1)
        ) {
            BQOverline(todayText(date))

            Text(
                text = buildAnnotatedString {
                    withStyle(BQFont.display(BQTypeScale.title1).toSpanStyle()) {
                        append("$points")
                    }

                    withStyle(BQFont.display(20.sp).toSpanStyle().copy(color = BQColors.amber)) {
                        append(" pts")
                    }
                }
            )
        }

        BQProgressRing(
            value = completedCount,
            total = doableCount,
            text = "$completedCount/$doableCount"
        )
    }
}

@Composable
private fun TodayTasks(
    occurrences: List<TodayOccurrence>,
    sendingTaskIds: Set<Int>,
    onCompleteTask: (TodayOccurrence) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (occurrences.isEmpty()) {
        EmptyCard(
            title = "Nenhuma tarefa hoje",
            message = "As tarefas dos seus desafios aparecem aqui no dia em que valem.",
            modifier = modifier
        )
        return
    }

    val groups = occurrences.groupBy { it.challengeId }.values

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp5)
    ) {
        groups.forEach { group ->
            Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp2)) {
                if (groups.size > 1) {
                    BQOverline(group.first().challengeName)
                }

                group.forEach { occurrence ->
                    TaskCard(
                        name = occurrence.name,
                        points = occurrence.points,
                        state = occurrence.state,
                        deadlineText = localTimeText(occurrence.deadline),
                        hasPhoto = occurrence.hasPhoto,
                        weekly = occurrence.weekly,
                        isSending = occurrence.taskId in sendingTaskIds,
                        onComplete = { onCompleteTask(occurrence) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PointsPop(points: Int, onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(points) {
        scale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
        delay(1200)
        alpha.animateTo(0f, tween(200))
        onFinished()
    }

    BQPointsPill(
        points = points,
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            this.alpha = alpha.value
        }
    )
}

@Composable
private fun ErrorToast(message: String, onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(message) {
        alpha.animateTo(1f, tween(200))
        delay(2500)
        alpha.animateTo(0f, tween(200))
        onFinished()
    }

    BQErrorToast(
        message = message,
        modifier = modifier.graphicsLayer { this.alpha = alpha.value }
    )
}

@Composable
private fun EmptyCard(title: String, message: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(BQRadius.medium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BQColors.bg1, shape)
            .border(1.dp, BQColors.stroke1, shape)
            .padding(horizontal = BQSpacing.cardPadding, vertical = BQSpacing.sp5),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp1)
    ) {
        Text(
            text = title,
            style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold),
            color = BQColors.text2
        )
        Text(
            text = message,
            style = BQFont.body(BQTypeScale.caption),
            color = BQColors.text3,
            textAlign = TextAlign.Center,
        )
    }
}