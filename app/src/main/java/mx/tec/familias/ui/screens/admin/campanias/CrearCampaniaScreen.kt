package mx.tec.familias.ui.screens.admin.campanias

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.components.PublicationPhotoPicker
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextOnPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun CrearCampaniaScreen(
    isSaving: Boolean = false,
    error: String? = null,
    onBackClick: () -> Unit = {},
    onCreate: (String, String, String, Uri?) -> Unit = { _, _, _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Crear campaña",
                titleMaxLines = 2,
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Nueva campaña",
                color = TealPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Completa los datos que se guardan en la campaña.",
                color = TextSecondary,
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                enabled = !isSaving
            )
            OutlinedTextField(
                value = organizationName,
                onValueChange = { organizationName = it },
                label = { Text("Nombre de la organización") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )
            Text(
                "Foto de campaña",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            PublicationPhotoPicker(
                photoUrl = null,
                selectedPhotoUri = photoUri,
                isUploading = isSaving,
                onPhotoSelected = { photoUri = it }
            )
            if (!error.isNullOrBlank()) {
                Text(error, color = BrownPrimary)
            }
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    onCreate(title, description, organizationName, photoUri)
                },
                enabled = !isSaving &&
                    title.trim().length >= 3 &&
                    description.isNotBlank() &&
                    organizationName.trim().length >= 2,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Surface,
                        strokeWidth = 2.dp
                    )
                    Text(" Guardando...", color = TextOnPrimary)
                } else {
                    Text("Crear campaña", color = TextOnPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
