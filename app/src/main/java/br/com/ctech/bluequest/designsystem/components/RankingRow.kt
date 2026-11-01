package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun RankingRow(
    position: Int,
    name: String,
    points: Int,
    isYou: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(BQRadius.medium)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(if (isYou) BQColors.blueDim else Color.Transparent, shape)
            .border(1.dp, if (isYou) BQColors.blue else Color.Transparent, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$position",
            modifier = Modifier.width(28.dp),
            style = BQFont.display(BQTypeScale.body),
            color = BQColors.medal(position),
            textAlign = TextAlign.Center,
        )

        BQAvatar(name = name, size = 36.dp)

        Text(
            text = buildAnnotatedString {
                append(name)
                if (isYou) {
                    withStyle(SpanStyle(color = BQColors.blueBright, fontWeight = FontWeight.Normal)) {
                        append(" · você")
                    }
                }
            },
            modifier = Modifier.weight(1f),
            style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold)
        )

        PointsText(points = points)
    }
}

@Composable
internal fun PointsText(points: Int) {
    Text(
        text = buildAnnotatedString {
            append("$points")
            withStyle(SpanStyle(fontSize = BQTypeScale.caption, fontWeight = FontWeight.Medium, color = BQColors.text3)) {
                append(" pts")
            }
        },
        style = BQFont.display(BQTypeScale.body),
    )
}