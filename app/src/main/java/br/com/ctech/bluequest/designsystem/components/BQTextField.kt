package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    isSecure: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val shape = RoundedCornerShape(BQRadius.small)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
            color = BQColors.text2,
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = BQFont.body(BQTypeScale.body).copy(color = BQColors.text1),
            cursorBrush = SolidColor(BQColors.blueBright),
            visualTransformation = if (isSecure) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .background(BQColors.bg1, shape)
                        .border(1.dp, BQColors.stroke1, shape)
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            tint = BQColors.text3,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(text = placeholder, style = BQFont.body(BQTypeScale.body), color = BQColors.text3)
                        }

                        innerTextField()
                    }
                }
            }
        )
    }
}