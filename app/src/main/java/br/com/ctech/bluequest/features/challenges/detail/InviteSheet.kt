package br.com.ctech.bluequest.features.challenges.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQButton
import kotlinx.coroutines.delay
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteSheet(
    challengeName: String,
    state: InviteSheetState,
    onPreview: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val invite = state.invite
    val link = invite?.link?.takeIf { invite.isEnabled }
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    val (linkText, linkColor) = when {
        invite != null && !invite.isEnabled -> "Novos convites estão desativados." to BQColors.text3
        link != null -> link to BQColors.text2
        state.errorMessage != null -> state.errorMessage to BQColors.red
        else -> "Gerando link..." to BQColors.text3
    }

    val boxShape = RoundedCornerShape(BQRadius.small)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = BQRadius.large, topEnd = BQRadius.large),
        containerColor = BQColors.bg1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = BQSpacing.screenPadding, end = BQSpacing.screenPadding, bottom = BQSpacing.sp6),
            verticalArrangement = Arrangement.spacedBy(BQSpacing.sp4)
        ) {
            Text(
                text = "Convidar para o desafio",
                style = BQFont.display(19.sp)
            )

            Text(
                text = "Quem receber o link entra direto no $challengeName. Convites de desafios encerrados são inválidos.",
                style = BQFont.body(BQTypeScale.caption),
                color = BQColors.text3
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BQColors.bg0, boxShape)
                    .border(1.dp, BQColors.stroke1, boxShape)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null,
                    tint = BQColors.text3,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = linkText,
                    style = BQFont.display(BQTypeScale.caption, FontWeight.Medium),
                    color = linkColor,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2)
            ) {
                BQButton(
                    title = if (copied) "Link copiado" else "Copiar link",
                    onClick = {
                        val clipboard = context.getSystemService(ClipboardManager::class.java)
                        clipboard.setPrimaryClip(ClipData.newPlainText("Convite", link))
                        copied = true
                    },
                    modifier = Modifier.weight(1f),
                    icon = if (copied) R.drawable.ic_check else R.drawable.ic_content_copy,
                    variant = BQButtonVariant.Secondary,
                    enabled = link != null,
                )

                BQButton(
                    title = "Compartilhar",
                    onClick = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, link)
                        }

                        context.startActivity(Intent.createChooser(send, null))
                    },
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_share,
                    enabled = link != null,
                )
            }

            BQButton(
                title = "Ver como o convidado recebe",
                onClick = {
                    invite?.code?.let { code ->
                        onDismiss()
                        onPreview(code)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = BQButtonVariant.Ghost,
                enabled = invite?.code != null,
            )

        }
    }
}