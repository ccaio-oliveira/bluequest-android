package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.domain.models.ChallengeState

@Composable
fun ChallengeCard(
    name: String,
    periodText: String,
    state: ChallengeState,
    day: Int,
    totalDays: Int,
    points: Int,
    rank: Int?,
    participantNames: List<String>,
    participantsCount: Int,
    isHero: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(if (isHero) BQRadius.large else BQRadius.medium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (state == ChallengeState.Closed) 0.7f else 1f)
            .then(
                if (isHero) {
                    Modifier.dropShadow(shape, Shadow(radius = 10.dp, color = BQColors.blue.copy(alpha = 0.35f), offset = DpOffset(0.dp, 4.dp)))
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(BQColors.bg1)
            .border(1.dp, if (isHero) BQColors.blue.copy(alpha = 0.4f) else BQColors.stroke1, shape)
            .clickable(onClick = onClick)
            .padding(BQSpacing.cardPadding),
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = name,
                    style = BQFont.display(if (isHero) BQTypeScale.title2 else BQTypeScale.headline),
                )
                Text(text = periodText, style = BQFont.body(BQTypeScale.caption), color = BQColors.text3)
            }

            when (state) {
                ChallengeState.Closed -> BQBadge(text = "Encerrado", tone = BQBadgeTone.Neutral)
                ChallengeState.Future -> BQBadge(text = "Em breve", tone = BQBadgeTone.Neutral)
                ChallengeState.InProgress -> if (rank != null) {
                    BQBadge(text = "${rank}º", tone = BQBadgeTone.Points, icon = R.drawable.ic_trophy_fill)
                }
            }
        }

        if (state != ChallengeState.Future) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BQProgressBar(value = day, total = totalDays)

                Row {
                    Text(
                        text = "Dia $day de $totalDays",
                        modifier = Modifier.weight(1f),
                        style = BQFont.body(12.sp),
                        color = BQColors.text3
                    )
                    Text(text = "$points pts", style = BQFont.display(12.sp, FontWeight.Bold), color = BQColors.amber)
                }
            }
        }

        if (participantNames.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    participantNames.take(4).forEach { BQAvatar(name = it) }
                }

                Text(
                    text = if (participantsCount == 1) "1 participante" else "$participantsCount participantes",
                    style = BQFont.body(12.sp),
                    color = BQColors.text3
                )
            }
        }
    }
}