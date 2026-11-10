package br.com.ctech.bluequest.features.challenges.invite

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQAvatar
import br.com.ctech.bluequest.designsystem.components.BQBadge
import br.com.ctech.bluequest.designsystem.components.BQBadgeTone
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonSize
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQEmptyState
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import br.com.ctech.bluequest.designsystem.components.BQOverline
import br.com.ctech.bluequest.domain.models.InvitePreview
import br.com.ctech.bluequest.domain.models.InviteState
import br.com.ctech.bluequest.features.challenges.periodText

@Composable
fun InviteRoute(
    code: String,
    onClose: () -> Unit,
    onOpenChallenge: (Int) -> Unit,
    viewModel: InviteViewModel = hiltViewModel<InviteViewModel, InviteViewModel.Factory>(
        key = code,
        creationCallback = { factory -> factory.create(code) }
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.acceptedChallengeId) {
        state.acceptedChallengeId?.let(onOpenChallenge)
    }

    InviteScreen(
        state = state,
        onClose = onClose,
        onAccept = viewModel::accept,
        onOpenChallenge = onOpenChallenge
    )
}

@Composable
fun InviteScreen(
    state: InviteUiState,
    onClose: () -> Unit,
    onAccept: () -> Unit,
    onOpenChallenge: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(BQRadius.large)
    val preview = state.preview

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BQSpacing.screenPadding, vertical = BQSpacing.sp2),
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp4),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BQOverline("Convite recebido", modifier = Modifier.weight(1f))

            BQIconButton(
                icon = R.drawable.ic_close,
                contentDescription = "Fechar",
                onClick = onClose
            )
        }

        if (preview == null && state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BQColors.text2)
            }

            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BQColors.bg1, shape)
                .border(1.dp, BQColors.stroke1, shape)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (preview?.state) {
                null -> InviteMessage(
                    icon = R.drawable.ic_warning_fill,
                    title = "Não foi possível abrir o convite",
                    message = state.errorMessage ?: "Tente novamente em instantes.",
                    actionTitle = "Voltar",
                    onAction = onClose
                )

                InviteState.Valid -> ValidInvite(
                    preview = preview,
                    isAccepting = state.isAccepting,
                    errorMessage = state.errorMessage,
                    onAccept = onAccept,
                    onClose = onClose,
                )

                InviteState.Invalid -> InviteMessage(
                    icon = R.drawable.ic_link,
                    title = "Convite inválido",
                    message = "O link está incorreto ou foi desativado por quem criou o desafio.",
                    actionTitle = "Voltar",
                    onAction = onClose,
                )

                InviteState.ChallengeClosed -> InviteMessage(
                    icon = R.drawable.ic_flag_fill,
                    title = "Desafio encerrado",
                    message = "Este desafio já terminou e não aceita novos participantes.",
                    actionTitle = "Voltar",
                    onAction = onClose,
                )
                InviteState.AlreadyParticipant -> InviteMessage(
                    icon = R.drawable.ic_check_circle_fill,
                    title = "Você já está neste desafio",
                    message = "Sua participação continua valendo — nada muda.",
                    actionTitle = "Abrir desafio",
                    onAction = { preview.challengeId?.let(onOpenChallenge) },
                )
            }
        }
    }
}

@Composable
private fun ValidInvite(
    preview: InvitePreview,
    isAccepting: Boolean,
    errorMessage: String?,
    onAccept: () -> Unit,
    onClose: () -> Unit,
) {
    val participants = if (preview.participantsCount == 1) "1 participante" else "${preview.participantsCount} participantes"
    val period = if (preview.startDate != null && preview.endDate != null) periodText(preview.startDate, preview.endDate) else null
    val tasks = if (preview.tasksCount == 1) "1 tarefa" else "${preview.tasksCount} tarefas"

    Row(
        horizontalArrangement = Arrangement.spacedBy((-10).dp)
    ) {
        preview.participantNames.take(4).forEach { name ->
            BQAvatar(name = name, size = 44.dp)
        }
    }

    Text(
        text = preview.name,
        style = BQFont.display(BQTypeScale.title2),
        textAlign = TextAlign.Center,
    )

    Text(
        text = listOfNotNull(preview.invitedBy?.let { "$it convidou você" }, period, participants).joinToString(" · "),
        style = BQFont.body(BQTypeScale.caption),
        color = BQColors.text3,
        textAlign = TextAlign.Center,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2)
    ) {
        BQBadge(
            text = "${preview.totalDays} dias",
            tone = BQBadgeTone.Available,
            icon = R.drawable.ic_event
        )

        BQBadge(
            text = "até ${preview.maxPointsPerDay} pts/dia",
            tone = BQBadgeTone.Points,
            icon = R.drawable.ic_bolt_fill
        )

        BQBadge(
            text = tasks,
            tone = BQBadgeTone.Neutral,
            icon = R.drawable.ic_check_circle_fill
        )
    }

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            style = BQFont.body(BQTypeScale.caption, FontWeight.Medium),
            color = BQColors.red,
            textAlign = TextAlign.Center,
        )
    }

    BQButton(
        title = "Entrar no desafio",
        onClick = onAccept,
        modifier = Modifier.fillMaxWidth(),
        size = BQButtonSize.Large,
        isLoading = isAccepting,
    )

    BQButton(
        title = "Agora não",
        onClick = onClose,
        modifier = Modifier.fillMaxWidth(),
        variant = BQButtonVariant.Ghost,
    )
}

@Composable
private fun InviteMessage(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    actionTitle: String,
    onAction: () -> Unit,
) {
    BQEmptyState(
        icon = icon,
        title = title,
        message = message,
        action = { BQButton(title = actionTitle, onClick = onAction, variant = BQButtonVariant.Secondary) }
    )
}