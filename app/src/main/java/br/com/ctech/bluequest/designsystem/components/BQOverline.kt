package br.com.ctech.bluequest.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQOverline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BQColors.text3,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = BQFont.body(BQTypeScale.micro, FontWeight.SemiBold).copy(letterSpacing = 0.08.em),
        color = color,
    )
}