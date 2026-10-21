package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing

enum class BQBadgeTone(val background: Color, val foreground: Color) {
    Points(BQColors.amberDim, BQColors.amber),
    Done(BQColors.greenDim, BQColors.green),
    Expired(BQColors.redDim, BQColors.red),
    Available(BQColors.blueDim, BQColors.blueBright),
    Neutral(BQColors.bg2, BQColors.text2),
}

@Composable
fun BQBadge(
    text: String,
    tone: BQBadgeTone,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
) {
    Row(
        modifier = modifier
            .height(24.dp)
            .background(tone.background, CircleShape)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = tone.foreground,
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = text,
            style = BQFont.display(12.sp, FontWeight.SemiBold),
            color = tone.foreground
        )
    }
}