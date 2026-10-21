package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont

private val palette = listOf(BQColors.blue, BQColors.amber, BQColors.green, BQColors.avatarPurple,
    BQColors.avatarCyan)

@Composable
fun BQAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp,
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val color = palette[(name.firstOrNull()?.code ?: 0) % palette.size]
    val fontSize = with(LocalDensity.current) { (size * 0.4f).toSp() }

    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
            .border(2.dp, BQColors.bg1, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, style = BQFont.display(fontSize), color = BQColors.onBlue)
    }
}