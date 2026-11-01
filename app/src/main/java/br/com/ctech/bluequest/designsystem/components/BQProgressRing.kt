package br.com.ctech.bluequest.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont

@Composable
fun BQProgressRing(
    value: Int,
    total: Int,
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    lineWidth: Dp = 6.dp,
) {
    val target = if (total > 0) (value.toFloat() / total).coerceIn(0f, 1f) else 0f
    val progress by animateFloatAsState(targetValue = target, label = "ringProgress")
    val fontSize = with(LocalDensity.current) { (size * 0.24f).toSp() }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = lineWidth.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(this.size.width - strokeWidth, this.size.height - strokeWidth)

            drawArc(
                color = BQColors.stroke1,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = BQColors.blueBright,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(text = text, style = BQFont.display(fontSize))
    }
}