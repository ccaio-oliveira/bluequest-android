package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors

@Composable
fun BQProgressBar(
    value: Int,
    total: Int,
    modifier: Modifier = Modifier,
    color: Color = BQColors.blueBright,
) {
    val progress = if (total > 0) (value.toFloat() / total).coerceIn(0f, 1f) else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CircleShape)
            .background(BQColors.stroke1)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(color, CircleShape)
        )
    }
}