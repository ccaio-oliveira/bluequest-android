package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun ParticipantRow(
    name: String,
    joinedText: String,
    points: Int,
    isCreator: Boolean,
    isYou: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(BQColors.bg1)
            .padding(horizontal = 14.dp, vertical = BQSpacing.sp2),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BQAvatar(name = name, size = 38.dp)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(name)
                        if (isYou) {
                            withStyle(SpanStyle(fontSize = BQTypeScale.caption, fontWeight = FontWeight.Normal, color = BQColors.blueBright)) {
                                append(" você")
                            }
                        }
                    },
                    style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold),
                )

                if (isCreator) {
                    BQBadge(text = "criador", tone = BQBadgeTone.Available, icon = R.drawable.ic_star_fill)
                }
            }

            Text(text = "entrou em $joinedText", style = BQFont.body(12.sp), color = BQColors.text3)
        }

        PointsText(points = points)
    }
}