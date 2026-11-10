package br.com.ctech.bluequest.features.challenges.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import br.com.ctech.bluequest.designsystem.components.BQErrorState
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import br.com.ctech.bluequest.designsystem.components.BQIconButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQListGroup
import br.com.ctech.bluequest.designsystem.components.BQOverline
import br.com.ctech.bluequest.designsystem.components.BQProgressBar
import br.com.ctech.bluequest.designsystem.components.BQSegmentedControl
import br.com.ctech.bluequest.designsystem.components.FeedRow
import br.com.ctech.bluequest.designsystem.components.ParticipantRow
import br.com.ctech.bluequest.designsystem.components.RankingRow
import br.com.ctech.bluequest.designsystem.components.StatTile
import br.com.ctech.bluequest.designsystem.components.StatTileTone
import br.com.ctech.bluequest.designsystem.components.TaskCard
import br.com.ctech.bluequest.domain.models.ChallengeDetail
import br.com.ctech.bluequest.domain.models.ChallengeFeedItem
import br.com.ctech.bluequest.domain.models.ChallengeParticipant
import br.com.ctech.bluequest.domain.models.ChallengeState
import br.com.ctech.bluequest.domain.models.OccurrenceState
import br.com.ctech.bluequest.features.challenges.dayMonthText
import br.com.ctech.bluequest.features.challenges.feedDayTitle
import br.com.ctech.bluequest.features.challenges.localTimeText
import br.com.ctech.bluequest.features.challenges.periodText
import br.com.ctech.bluequest.features.challenges.recurrenceText
import br.com.ctech.bluequest.features.challenges.timeText

private enum class DetailSection(val title: String) {
    Ranking("Ranking"),
    You("Você"),
    People("Pessoas"),
    Tasks("Tarefas"),
    Feed("Feed"),
}

@Composable
fun ChallengeDetailRoute(
    challengeId: Int,
    onBack: () -> Unit,
    onPreviewInvite: (String) -> Unit,
    viewModel: ChallengeDetailViewModel = hiltViewModel<ChallengeDetailViewModel, ChallengeDetailViewModel.Factory>(creationCallback = { factory -> factory.create(challengeId) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose {  }
    }

    ChallengeDetailScreen(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::load,
        onFeedVisible = viewModel::onFeedVisible,
        onLoadMoreFeed = viewModel::loadMoreFeed,
        onOpenInvite = viewModel::onOpenInvite,
    )

    state.inviteSheet?.let { inviteSheet ->
        InviteSheet(
            challengeName = state.detail?.name.orEmpty(),
            state = inviteSheet,
            onPreview = onPreviewInvite,
            onDismiss = viewModel::onDismissInvite,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    state: ChallengeDetailUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onFeedVisible: () -> Unit,
    onLoadMoreFeed: () -> Unit,
    onOpenInvite: () -> Unit,
) {
    val detail = state.detail
    var section by rememberSaveable { mutableStateOf(DetailSection.Ranking) }
    var openPhotoItemId by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(section) {
        if (section == DetailSection.Feed) onFeedVisible()
    }

    if (detail == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackButton(onBack = onBack, modifier = Modifier.padding(start = BQSpacing.screenPadding, top = BQSpacing.sp2))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (state.errorMessage != null) {
                    BQErrorState(message = state.errorMessage, onRetry = onRetry)
                } else {
                    CircularProgressIndicator(color = BQColors.text2)
                }
            }
        }

        return
    }

    PullToRefreshBox(
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
                bottom = BQSpacing.sp6
            ),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Header(detail = detail, onBack = onBack, onOpenInvite = onOpenInvite)
                    Progress(detail = detail)
                    BQSegmentedControl(
                        options = DetailSection.entries.map { it.title },
                        selectedIndex = section.ordinal,
                        onSelect = { section = DetailSection.entries[it] },
                    )
                }
            }

            if (section == DetailSection.Feed) {
                feedSection(
                    state = state,
                    onOpenPhoto = { openPhotoItemId = it.id },
                    onLoadMore = onLoadMoreFeed,
                )
            } else {
                item {
                    Box(modifier = Modifier.padding(top = 18.dp)) {
                        when (section) {
                            DetailSection.Ranking -> RankingSection(detail)
                            DetailSection.You -> YouSection(detail)
                            DetailSection.People -> PeopleSection(detail)
                            DetailSection.Tasks -> TasksSection(detail)
                            DetailSection.Feed -> Unit
                        }
                    }
                }
            }
        }
    }

    state.feedItems?.firstOrNull { it.id == openPhotoItemId }?.let { item ->
        item.photoUrl?.let { url ->
            PhotoViewer(
                url = url,
                caption = "${item.name} · ${item.taskName} · ${dayMonthText(item.occurrenceDate)}, ${localTimeText(item.completedAt)}",
                onClose = { openPhotoItemId = null }
            )
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    BQIconButton(
        icon = R.drawable.ic_arrow_back,
        contentDescription = "Voltar",
        onClick = onBack,
        modifier = modifier
    )
}

@Composable
private fun Header(detail: ChallengeDetail, onBack: () -> Unit, onOpenInvite: () -> Unit) {
    val participants = if (detail.participantsCount == 1) "1 participante" else "${detail.participantsCount} participantes"

    Row(
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackButton(onBack = onBack)

        Column(modifier = Modifier.weight(1f)) {
            Text(text = detail.name, style = BQFont.display(BQTypeScale.title2))
            Text(
                text = "${periodText(detail.startDate, detail.endDate)} · $participants",
                style = BQFont.body(BQTypeScale.caption),
                color = BQColors.text3,
                maxLines = 2
            )
        }

        BQIconButton(
            icon = R.drawable.ic_person_add,
            contentDescription = "Convidar",
            onClick = onOpenInvite,
            variant = BQIconButtonVariant.Primary,
        )
    }
}

@Composable
private fun Progress(detail: ChallengeDetail) {
    val remaining = (detail.totalDays - detail.currentDay).coerceAtLeast(0)
    val remainingText = when {
        detail.state == ChallengeState.Closed -> "encerrado"
        remaining == 0 -> "último dia"
        else -> "termina em $remaining dias"
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BQProgressBar(value = detail.currentDay, total = detail.totalDays)

        Row {
            Text(
                text = "Dia ${detail.currentDay} de ${detail.totalDays}",
                modifier = Modifier.weight(1f),
                style = BQFont.body(12.sp),
                color = BQColors.text3
            )
            Text(text = remainingText, style = BQFont.body(12.sp), color = BQColors.text3)
        }
    }
}

@Composable
private fun RankingSection(detail: ChallengeDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        detail.participants.forEach { participant ->
            RankingRow(
                position = participant.position,
                name = participant.name,
                points = participant.points,
                isYou = participant.isYou
            )
        }
    }
}

@Composable
private fun YouSection(detail: ChallengeDetail) {
    val stats = detail.stats
    val total = stats.completedCount + stats.expiredCount
    val cardShape = RoundedCornerShape(BQRadius.medium)

    Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp3)) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2)
        ) {
            StatTile(
                icon = R.drawable.ic_bolt_fill,
                value = "${stats.points}",
                label = "pontos",
                tone = StatTileTone.Points,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            StatTile(
                icon = R.drawable.ic_trophy_fill,
                value = stats.position?.let { "${it}º" } ?: "–",
                label = "posição",
                tone = StatTileTone.Primary,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2)
        ) {
            StatTile(
                icon = R.drawable.ic_check_circle_fill,
                value = "${stats.completedCount}",
                label = "tarefas concluídas",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatTile(
                icon = R.drawable.ic_fire_fill,
                value = "${stats.streakDays}",
                label = "dias seguidos",
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BQColors.bg1, cardShape)
                .border(1.dp, BQColors.stroke1, cardShape)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Aproveitamento do período",
                style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
                color = BQColors.text2,
            )
            BQProgressBar(value = stats.completedCount, total = total, color = BQColors.amber)
            Text(
                text = "${stats.completedCount} de $total ocorrências concluídas · ${stats.expiredCount} expiradas",
                style = BQFont.body(12.sp),
                color = BQColors.text3,
            )
        }
    }
}

@Composable
private fun PeopleSection(detail: ChallengeDetail) {
    val people = detail.participants.sortedWith(
        compareByDescending<ChallengeParticipant> { it.isCreator }.thenByDescending { it.points }
    )

    BQListGroup {
        people.forEach { person ->
            ParticipantRow(
                name = person.name,
                joinedText = dayMonthText(person.joinedAt),
                points = person.points,
                isCreator = person.isCreator,
                isYou = person.isYou,
            )
        }
    }
}

@Composable
private fun TasksSection(detail: ChallengeDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp2)) {
        Text(text = "Tarefas configuradas neste desafio", style = BQFont.body(12.sp), color = BQColors.text3)

        detail.tasks.forEach { task ->
            TaskCard(
                name = task.name,
                points = task.points,
                state = OccurrenceState.Available,
                deadlineText = timeText(task.deadline),
                hasPhoto = task.hasPhoto
            )
        }

        Text(
            text = "Recorrências: " + detail.tasks.joinToString(" · ") { "${it.name.lowercase()} ${recurrenceText(it.recurrence)}" },
            style = BQFont.body(12.sp),
            color = BQColors.text3
        )
    }
}

private fun LazyListScope.feedSection(
    state: ChallengeDetailUiState,
    onOpenPhoto: (ChallengeFeedItem) -> Unit,
    onLoadMore: () -> Unit,
) {
    val feed = state.feedItems

    if (feed.isNullOrEmpty()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BQSpacing.sp5),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isLoadingFeed) {
                    CircularProgressIndicator(color = BQColors.text3)
                } else {
                    Text(
                        text = state.feedError ?: "Nenhuma conclusão ainda. Quando alguém concluir uma tarefa, ela aparece aqui.",
                        style = BQFont.body(BQTypeScale.caption),
                        color = if (state.feedError != null) BQColors.red else BQColors.text3,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        return
    }

    feed.groupBy { it.occurrenceDate }.forEach { (date, dayItems) ->
        item(key = "feed-day-$date") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BQSpacing.sp5),
                contentAlignment = Alignment.Center,
            ) {
                BQOverline(feedDayTitle(date))
            }
        }

        items(dayItems, key = { it.id }) { item ->
            FeedRow(
                taskName = item.taskName,
                points = item.points,
                name = item.name,
                isYou = item.isYou,
                timeText = localTimeText(item.completedAt),
                photoUrl = item.photoUrl,
                onOpenPhoto = { onOpenPhoto(item) },
                modifier = Modifier.padding(top = BQSpacing.sp2),
            )
        }
    }

    if (state.feedError != null) {
        item {
            Text(
                text = state.feedError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BQSpacing.sp3),
                style = BQFont.body(BQTypeScale.caption),
                color = BQColors.red,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (state.feedCursor != null) {
        item {
            BQButton(
                title = "Ver mais antigas",
                onClick = onLoadMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = BQSpacing.sp3),
                variant = BQButtonVariant.Ghost,
                isLoading = state.isLoadingFeed,
            )
        }
    }
}