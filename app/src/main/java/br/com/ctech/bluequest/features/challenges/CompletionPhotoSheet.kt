package br.com.ctech.bluequest.features.challenges

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonSize
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletionPhotoSheet(
    taskName: String,
    points: Int,
    onPhotoSelected: (Uri) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val hasCamera = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        cameraUri?.takeIf { saved }?.let(onPhotoSelected)
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { photo ->
        photo?.let(onPhotoSelected)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = BQRadius.large, topEnd = BQRadius.large),
        containerColor = BQColors.bg1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = BQSpacing.screenPadding, end = BQSpacing.screenPadding, bottom = BQSpacing.sp6),
            verticalArrangement = Arrangement.spacedBy(BQSpacing.sp3)
        ) {
            Text(
                text = "Concluir: $taskName",
                style = BQFont.display(16.sp, FontWeight.SemiBold)
            )

            Text(
                text = "Esta tarefa exige foto · +$points pts",
                style = BQFont.body(BQTypeScale.caption),
                color = BQColors.text3
            )

            if (hasCamera) {
                BQButton(
                    title = "Tirar foto",
                    onClick = {
                        val uri = newCameraUri(context)
                        cameraUri = uri
                        camera.launch(uri)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.ic_photo_camera_fill,
                    size = BQButtonSize.Large
                )
            }

            BQButton(
                title = "Escolher da galeria",
                onClick = {
                    gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.ic_photo_library,
                variant = BQButtonVariant.Secondary,
                size = BQButtonSize.Large
            )
        }
    }
}

private fun newCameraUri(context: Context): Uri {
    val directory = File(context.cacheDir, "camera").apply { mkdirs() }

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(directory, "foto.jpg"))
}