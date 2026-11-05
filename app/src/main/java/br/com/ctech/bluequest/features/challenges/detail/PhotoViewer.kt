package br.com.ctech.bluequest.features.challenges.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import coil3.compose.AsyncImage

private const val MAX_ZOOM = 4f
private const val DOUBLE_TAP_ZOOM = 2.5f

@Composable
fun PhotoViewer(url: String, caption: String, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        var containerSize by remember { mutableStateOf(IntSize.Zero) }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var isLoading by remember { mutableStateOf(true) }
        var failed by remember { mutableStateOf(false) }

        fun clamped(value: Offset, atScale: Float): Offset {
            val maxX = containerSize.width * (atScale - 1) / 2
            val maxY = containerSize.height * (atScale - 1) / 2

            return Offset(value.x.coerceIn(-maxX, maxX), value.y.coerceIn(-maxY, maxY))
        }

        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, MAX_ZOOM)
            offset = clamped(offset + panChange * scale, scale)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onSizeChanged { containerSize = it },
        ) {
            AsyncImage(
                model = url,
                contentDescription = caption,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale > 1f) 1f else DOUBLE_TAP_ZOOM
                                offset = Offset.Zero
                            },
                        )
                    }
                    .transformable(transformState, canPan = { scale > 1f })
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    },
                contentScale = ContentScale.Fit,
                onSuccess = { isLoading = false },
                onError = {
                    isLoading = false
                    failed = true
                },
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BQColors.text3,
                )
            }

            if (failed) {
                Text(
                    text = "Não foi possível carregar a foto.",
                    modifier = Modifier.align(Alignment.Center),
                    style = BQFont.body(BQTypeScale.caption),
                    color = BQColors.text2,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                BQIconButton(
                    icon = R.drawable.ic_close,
                    contentDescription = "Fechar",
                    onClick = onClose,
                    modifier = Modifier.padding(start = BQSpacing.screenPadding, top = BQSpacing.sp2),
                )

                if (scale == 1f) {
                    Text(
                        text = caption,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = BQSpacing.screenPadding, end = BQSpacing.screenPadding, bottom = BQSpacing.sp4),
                        style = BQFont.body(BQTypeScale.caption, FontWeight.Medium),
                        color = BQColors.text2
                    )
                }
            }
        }
    }
}