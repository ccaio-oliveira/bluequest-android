package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing

enum class StatTileTone(val valueColor: Color) {
    Neutral(BQColors.text1),
    Points(BQColors.amber),
    Primary(BQColors.blueBright)
}

@Composable
fun StatTile(
    @DrawableRes icon: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    tone: StatTileTone = StatTileTone.Neutral
) {
    val shape = RoundedCornerShape(BQRadius.medium)

    Column(
        modifier = modifier
            .background(BQColors.bg1, shape)
            .border(1.dp, BQColors.stroke1, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp1)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = BQColors.text3,
            modifier = Modifier.size(20.dp)
        )
        Text(text = value, style = BQFont.display(24.sp), color = tone.valueColor)
        Text(text = label, style = BQFont.body(12.sp), color = BQColors.text3)
    }
}