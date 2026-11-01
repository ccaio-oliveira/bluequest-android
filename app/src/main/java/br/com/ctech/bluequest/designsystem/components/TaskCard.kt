package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.domain.models.OccurrenceState
import br.com.ctech.bluequest.domain.models.WeeklyProgress

@Composable
fun TaskCard(
    name: String,
    points: Int,
    state: OccurrenceState,
    deadlineText: String,
    hasPhoto: Boolean,
    modifier: Modifier = Modifier,
    weekly: WeeklyProgress? = null,
    isSending: Boolean = false,
    onComplete: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(BQRadius.medium)
    val isAvailable = state == OccurrenceState.Available
    val isMandatory = isAvailable && weekly?.isMandatory == true && !isSending

    val meta = when {
        isSending -> "Aguardando envio"
        weekly != null -> weeklyText(state, weekly, deadlineText)
        state == OccurrenceState.Expired -> "Expirou às $deadlineText"
        state == OccurrenceState.Completed -> "Concluída"
        state == OccurrenceState.Future -> "Disponível em breve"
        else -> "Disponível até $deadlineText"
    }

    val metaColor = when {
        isSending -> BQColors.blueBright
        state == OccurrenceState.Expired -> BQColors.red
        isMandatory -> BQColors.amber
        else -> BQColors.text3
    }

    val borderColor = when {
        isMandatory -> BQColors.amber
        isAvailable || isSending -> BQColors.stroke2
        else -> BQColors.stroke1
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isAvailable || isSending) 1f else 0.6f)
            .background(BQColors.bg1, shape)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StateButton(state = state, isSending = isSending, onComplete = onComplete)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                style = BQFont.body(16.sp, FontWeight.SemiBold).copy(
                    textDecoration = if (state == OccurrenceState.Completed) TextDecoration.LineThrough else null,
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMandatory) {
                    Icon(
                        painter = painterResource(R.drawable.ic_warning_fill),
                        contentDescription = null,
                        tint = BQColors.amber,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = meta,
                    modifier = Modifier.weight(1f, fill = false),
                    style = BQFont.body(BQTypeScale.caption),
                    color = metaColor
                )

                if (hasPhoto && !isSending) {
                    Icon(
                        painter = painterResource(R.drawable.ic_photo_camera_fill),
                        contentDescription = "Exige foto",
                        tint = metaColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        BQBadge(
            text = "+$points pts",
            tone = if (state == OccurrenceState.Completed) BQBadgeTone.Points else BQBadgeTone.Neutral,
            icon = R.drawable.ic_bolt_fill
        )
    }
}

private fun weeklyText(state: OccurrenceState, weekly: WeeklyProgress,  deadlineText: String): String {
    val progress = "${weekly.done} de ${weekly.target} na semana"

    return when (state) {
        OccurrenceState.Completed -> "Concluída · $progress"
        OccurrenceState.Expired -> "Meta da semana não batida"
        OccurrenceState.Future -> progress
        OccurrenceState.Available -> {
            if (weekly.isMandatory) {
                val days = if (weekly.daysLeft == 1) "1 dia" else "${weekly.daysLeft} dias"
                "Obrigatória · faltam ${weekly.remaining} em $days"
            } else {
                "$progress · até $deadlineText"
            }
        }
    }
}

@Composable
private fun StateButton(state: OccurrenceState, isSending: Boolean, onComplete: (() -> Unit)?) {
    val (icon, tint, background) = when {
        isSending -> Triple(R.drawable.ic_arrow_upward, BQColors.blueBright, BQColors.blueDim)
        state == OccurrenceState.Completed -> Triple(R.drawable.ic_check, BQColors.green, BQColors.greenDim)
        state == OccurrenceState.Expired -> Triple(R.drawable.ic_close, BQColors.red, BQColors.redDim)
        state == OccurrenceState.Future -> Triple(R.drawable.ic_schedule, BQColors.text3, BQColors.bg2)
        else -> Triple(R.drawable.ic_radio_unchecked, BQColors.blueBright, BQColors.bg2)
    }
    val isAvailable = state == OccurrenceState.Available && !isSending

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background)
            .border(2.dp, if (isAvailable) BQColors.blueBright else Color.Transparent, CircleShape)
            .then(
                if (isAvailable && onComplete != null) {
                    Modifier.clickable(role = Role.Button, onClickLabel = "Concluir", onClick = onComplete)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}