package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCampaniaScreen(
    campaignId: Int,
    campaigns: List<CampaignResponse>,
    isSaving: Boolean = false,
    error: String? = null,
    onBackClick: () -> Unit = {},
    onSave: (Int, CampaignUpdateRequest) -> Unit = { _, _ -> }
) {
    val campaign = campaigns.find { it.id == campaignId }

    var title by remember(campaign) { mutableStateOf(campaign?.title ?: "") }
    var description by remember(campaign) { mutableStateOf(campaign?.description ?: "") }
    var organizationName by remember(campaign) { mutableStateOf(campaign?.organization_name ?: "") }
    var status by remember(campaign) { mutableStateOf(campaign?.status ?: "borrador") }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Editar campaña",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (campaign == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Campaña no encontrada", color = TextSecondary, fontSize = 16.sp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la campaña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = TealPrimary
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = TealPrimary
                    )
                )

                OutlinedTextField(
                    value = organizationName,
                    onValueChange = { organizationName = it },
                    label = { Text("Nombre de la Organización") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = TealPrimary
                    )
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Estado (borrador, publicada, etc.)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = TealPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!error.isNullOrBlank()) {
                    Text(
                        text = error,
                        color = BrownPrimary,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        val request = CampaignUpdateRequest(
                            title = title.ifBlank { null },
                            description = description.ifBlank { null },
                            organization_name = organizationName.ifBlank { null },
                            status = status
                                .takeIf { it != campaign.status }
                                ?.ifBlank { null }
                        )
                        onSave(campaignId, request)
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Surface,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardando...", color = TextOnPrimary)
                    } else {
                        Text(
                            "Guardar Cambios",
                            color = TextOnPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
