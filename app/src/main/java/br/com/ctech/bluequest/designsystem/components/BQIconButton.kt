package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQSpacing

@Composable
fun BQIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(BQSpacing.hitTarget)
            .clip(CircleShape)
            .background(BQColors.bg2)
            .clickable(role = Role.Button, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = BQColors.text2,
            modifier = Modifier.size(20.dp)
        )
    }
}