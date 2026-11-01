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

@Composable
fun TaskCard(
    name: String,
    points: Int,
    state: OccurrenceState,
    deadlineText: String,
    hasPhoto: Boolean,
    modifier: Modifier = Modifier,
    onComplete: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(BQRadius.medium)
    val isAvailable = state == OccurrenceState.Available

    val meta = when (state) {
        OccurrenceState.Expired -> "Expirou às $deadlineText"
        OccurrenceState.Completed -> "Concluída"
        OccurrenceState.Future -> "Disponível em breve"
        OccurrenceState.Available -> "Disponível até $deadlineText"
    }

    val metaColor = if (state == OccurrenceState.Expired) BQColors.red else BQColors.text3

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isAvailable) 1f else 0.6f)
            .background(BQColors.bg1, shape)
            .border(1.dp, if (isAvailable) BQColors.stroke2 else BQColors.stroke1, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StateButton(state = state, onComplete = onComplete)

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
                Text(text = meta, style = BQFont.body(BQTypeScale.caption), color = metaColor)

                if (hasPhoto) {
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

@Composable
private fun StateButton(state: OccurrenceState, onComplete: (() -> Unit)?) {
    val (icon, tint, background) = when (state) {
        OccurrenceState.Completed -> Triple(R.drawable.ic_check, BQColors.green, BQColors.greenDim)
        OccurrenceState.Expired -> Triple(R.drawable.ic_close, BQColors.red, BQColors.redDim)
        OccurrenceState.Future -> Triple(R.drawable.ic_schedule, BQColors.text3, BQColors.bg2)
        OccurrenceState.Available -> Triple(R.drawable.ic_radio_unchecked, BQColors.blueBright, BQColors.bg2)
    }
    val canComplete = state == OccurrenceState.Available && onComplete != null

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background)
            .border(2.dp, if (state == OccurrenceState.Available) BQColors.blueBright else Color.Transparent, CircleShape)
            .then(
                if (canComplete) {
                    Modifier.clickable(role = Role.Button, onClickLabel = "Concluir", onClick = { onComplete?.invoke() })
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}