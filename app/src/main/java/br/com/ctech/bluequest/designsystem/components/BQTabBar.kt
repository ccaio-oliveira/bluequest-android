package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale

data class BQTabBarItem(
    val label: String,
    val icon: Int,
    val selectedIcon: Int,
)

@Composable
fun BQTabBar(
    items: List<BQTabBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BQColors.bg0)
    ) {
        HorizontalDivider(thickness = 1.dp, color = BQColors.stroke1)

        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(BQSpacing.sp2)
        ) {
            items.forEachIndexed { index, item ->
                val isActive = index == selectedIndex
                val color = if (isActive) BQColors.blueBright else BQColors.text3

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = BQSpacing.hitTarget)
                        .selectable(
                            selected = isActive,
                            onClick = { onSelect(index) },
                            role = Role.Tab,
                        )
                        .padding(vertical = BQSpacing.sp1),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(if (isActive) item.selectedIcon else item.icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(26.dp)
                    )

                    Text(
                        text = item.label,
                        style = BQFont.body(BQTypeScale.micro, FontWeight.SemiBold),
                        color = color
                    )
                }
            }
        }
    }
}