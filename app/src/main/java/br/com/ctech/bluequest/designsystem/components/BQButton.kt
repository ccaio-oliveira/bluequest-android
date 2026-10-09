package br.com.ctech.bluequest.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing

enum class BQButtonVariant(val background: Color, val foreground: Color) {
    Primary(BQColors.blue, BQColors.onBlue),
    Secondary(BQColors.blueDim, BQColors.blueBright),
    Ghost(Color.Transparent, BQColors.blueBright),
    Danger(BQColors.redDim, BQColors.red),
}

enum class BQButtonSize(val height: Dp, val fontSize: TextUnit, val horizontalPadding: Dp) {
    Large(52.dp, 17.sp, 24.dp),
    Medium(44.dp, 15.sp, 18.dp),
    Small(34.dp, 13.sp, 14.dp);

    val cornerRadius: Dp get() = if (this == Small) BQRadius.small else BQRadius.medium
}

@Composable
fun BQButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BQButtonVariant = BQButtonVariant.Primary,
    size: BQButtonSize = BQButtonSize.Medium,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "pressScale",
    )
    val isEnabled = enabled && !isLoading

    Row(
        modifier = modifier
            .height(size.height)
            .scale(scale)
            .alpha(if (isEnabled) 1f else 0.4f)
            .clip(RoundedCornerShape(size.cornerRadius))
            .background(variant.background)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = isEnabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = size.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = variant.foreground,
                strokeWidth = 2.dp,
            )
        }

        Text(
            text = title,
            style = BQFont.display(size.fontSize, FontWeight.SemiBold),
            color = variant.foreground,
        )
    }
}