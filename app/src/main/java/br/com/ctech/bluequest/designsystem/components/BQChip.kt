package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) BQColors.blue else BQColors.bg2)
            .border(1.dp, if (isSelected) BQColors.blue else BQColors.stroke1, CircleShape)
            .selectable(selected = isSelected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
            color = if (isSelected) BQColors.onBlue else BQColors.text2,
        )
    }
}