package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(BQRadius.small)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BQColors.bg1, shape)
            .border(1.dp, BQColors.stroke1, shape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isActive = index == selectedIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) BQColors.bg2 else Color.Transparent)
                    .selectable(
                        selected = isActive,
                        onClick = { onSelect(index) },
                        role = Role.Tab
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
                    color = if (isActive) BQColors.text1 else BQColors.text3
                )
            }
        }
    }
}
