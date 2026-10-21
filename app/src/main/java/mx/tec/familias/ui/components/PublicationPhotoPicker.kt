package mx.tec.familias.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun PublicationPhotoPicker(
    photoUrl: String?,
    selectedPhotoUri: Uri?,
    isUploading: Boolean = false,
    onPhotoSelected: (Uri) -> Unit
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let(onPhotoSelected) }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            enabled = !isUploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = TealPrimary)
                Text(if (isUploading) "Subiendo imagen..." else "Elegir foto")
            }
        }
        Text(
            when {
                isUploading -> "La imagen se está subiendo."
                selectedPhotoUri != null -> "Nueva imagen seleccionada."
                !photoUrl.isNullOrBlank() -> "Esta publicación ya tiene una foto."
                else -> "Puedes agregar una imagen JPEG, PNG o WebP (máximo 10 MB)."
            },
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
