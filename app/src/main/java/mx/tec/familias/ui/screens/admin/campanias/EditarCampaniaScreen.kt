package mx.tec.familias.ui.screens.admin.campanias

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.components.PublicationPhotoPicker
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextOnPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.EditablePublication
import mx.tec.familias.viewmodel.PublicationKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCampaniaScreen(
    campaignId: Int,
    campaigns: List<CampaignResponse>,
    activities: List<EditablePublication> = emptyList(),
    canChangeStatus: Boolean = false,
    isSaving: Boolean = false,
    isSavingActivity: Boolean = false,
    error: String? = null,
    activityError: String? = null,
    onBackClick: () -> Unit = {},
    onSave: (Int, CampaignUpdateRequest, Uri?) -> Unit = { _, _, _ -> },
    onSaveActivity: (EditablePublication, Uri?) -> Unit = { _, _ -> }
) {
    val campaign = campaigns.find { it.id == campaignId }
    val editableActivities = activities.filter {
        it.kind == PublicationKind.ACTIVITY
    }
    var selectedActivityId by remember(campaignId) {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(editableActivities, campaignId) {
        if (editableActivities.none { it.id == selectedActivityId }) {
            selectedActivityId = editableActivities
                .firstOrNull { it.campaignId == campaignId }
                ?.id
                ?: editableActivities.firstOrNull()?.id
        }
    }
    val selectedActivity = editableActivities.find {
        it.id == selectedActivityId
    }

    var title by remember(campaign) { mutableStateOf(campaign?.title.orEmpty()) }
    var description by remember(campaign) {
        mutableStateOf(campaign?.description.orEmpty())
    }
    var campaignPhotoUri by remember(campaign) { mutableStateOf<Uri?>(null) }
    var organizationName by remember(campaign) {
        mutableStateOf(campaign?.organization_name.orEmpty())
    }
    var status by remember(campaign) {
        mutableStateOf(campaign?.status ?: "borrador")
    }
    var statusExpanded by remember(campaign) { mutableStateOf(false) }
    var activityMenuExpanded by remember(campaignId) {
        mutableStateOf(false)
    }
    var activityTitle by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.title.orEmpty())
    }
    var activityDescription by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.description.orEmpty())
    }
    var activityPhotoUri by remember(selectedActivity) {
        mutableStateOf<Uri?>(null)
    }
    var startsAt by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.startsAt.orEmpty())
    }
    var endsAt by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.endsAt.orEmpty())
    }
    var registrationDeadline by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.registrationDeadline.orEmpty())
    }
    var location by remember(selectedActivity) {
        mutableStateOf(selectedActivity?.location.orEmpty())
    }
    var capacity by remember(selectedActivity) {
        mutableIntStateOf(selectedActivity?.capacityPeople?.toIntOrNull() ?: 1)
    }
    var activityValidationError by remember(selectedActivity) {
        mutableStateOf<String?>(null)
    }
    val statusOptions = when (campaign?.status) {
        "borrador" -> listOf("borrador", "publicada", "cancelada")
        "publicada" -> listOf("publicada", "cancelada", "finalizada")
        else -> listOf(campaign?.status ?: "borrador")
    }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Editar campaña",
                titleMaxLines = 2,
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
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Campaña no encontrada",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Editar campaña",
                    color = TealPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Actualiza la campaña y, por separado, selecciona una actividad para modificar sus fechas, ubicación o capacidad.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                EditSectionCard(
                    title = "Información de la campaña",
                    icon = { }
                ) {
                    CampaignField(
                        value = title,
                        label = "Título de la campaña",
                        onValueChange = { title = it }
                    )
                    CampaignField(
                        value = description,
                        label = "Descripción",
                        onValueChange = { description = it },
                        minLines = 3
                    )
                    CampaignField(
                        value = organizationName,
                        label = "Nombre de la organización",
                        onValueChange = { organizationName = it }
                    )
                    Text(
                        "Foto de campaña",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    PublicationPhotoPicker(
                        photoUrl = campaign.photo_url,
                        selectedPhotoUri = campaignPhotoUri,
                        isUploading = isSaving,
                        onPhotoSelected = { campaignPhotoUri = it }
                    )
                    if (canChangeStatus) {
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Estado") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = statusExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(18.dp),
                                colors = campaignFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                statusOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            status = option
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Estado actual: $status",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                    if (!error.isNullOrBlank()) {
                        Text(error, color = BrownPrimary, fontSize = 14.sp)
                    }
                    Button(
                        onClick = {
                            onSave(
                                campaignId,
                                CampaignUpdateRequest(
                                    title = title.trim().takeIf(String::isNotEmpty),
                                    description = description.trim()
                                        .takeIf(String::isNotEmpty),
                                    organization_name = organizationName.trim()
                                        .takeIf(String::isNotEmpty),
                                    status = status.takeIf {
                                        canChangeStatus && it != campaign.status
                                    }
                                ),
                                campaignPhotoUri
                            )
                        },
                        enabled = !isSaving &&
                            title.trim().length >= 3 &&
                            description.isNotBlank() &&
                            organizationName.trim().length >= 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary
                        )
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Surface,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.size(8.dp))
                        }
                        Text(
                            if (isSaving) "Guardando campaña..." else "Guardar campaña",
                            color = TextOnPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                EditSectionCard(
                    title = "Fecha y horario de actividad",
                    icon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    if (editableActivities.isEmpty()) {
                        Text(
                            "No hay actividades disponibles para editar.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = activityMenuExpanded,
                            onExpandedChange = {
                                activityMenuExpanded = it
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedActivity?.title.orEmpty(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Actividad") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = activityMenuExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(18.dp),
                                colors = campaignFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = activityMenuExpanded,
                                onDismissRequest = {
                                    activityMenuExpanded = false
                                }
                            ) {
                                editableActivities.forEach { activity ->
                                    DropdownMenuItem(
                                        text = { Text(activity.title) },
                                        onClick = {
                                            selectedActivityId = activity.id
                                            activityMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (selectedActivity != null) {
                            CampaignField(
                                value = activityTitle,
                                label = "Título de la actividad",
                                onValueChange = { activityTitle = it }
                            )
                            CampaignField(
                                value = activityDescription,
                                label = "Descripción de la actividad",
                                onValueChange = { activityDescription = it },
                                minLines = 3
                            )
                            CampaignField(
                                value = startsAt,
                                label = "Fecha y hora de inicio (ISO 8601)",
                                onValueChange = { startsAt = it },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = TextSecondary
                                    )
                                }
                            )
                            Text(
                                "Ejemplo: 2026-10-20T09:00:00-06:00",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            CampaignField(
                                value = endsAt,
                                label = "Fecha y hora de fin (ISO 8601)",
                                onValueChange = { endsAt = it }
                            )
                            CampaignField(
                                value = registrationDeadline,
                                label = "Fecha límite de inscripción (ISO 8601)",
                                onValueChange = { registrationDeadline = it }
                            )
                            Text(
                                "Foto de actividad",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            PublicationPhotoPicker(
                                photoUrl = selectedActivity.photoUrl,
                                selectedPhotoUri = activityPhotoUri,
                                isUploading = isSavingActivity,
                                onPhotoSelected = { activityPhotoUri = it }
                            )
                        }
                    }
                }

                EditSectionCard(
                    title = "Ubicación",
                    icon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    if (selectedActivity != null) {
                        CampaignField(
                            value = location,
                            label = "Dirección o punto de encuentro",
                            onValueChange = { location = it },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        )
                    } else {
                        Text(
                            "Selecciona una actividad para editar su ubicación.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                EditSectionCard(
                    title = "Participación",
                    icon = {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    if (selectedActivity != null) {
                        Text(
                            "Capacidad máxima de personas para esta actividad.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TextButton(
                                onClick = { if (capacity > 1) capacity-- }
                            ) {
                                Text("−", color = TealPrimary, fontSize = 22.sp)
                            }
                            Text(
                                capacity.toString(),
                                color = TealPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = { capacity++ }
                            ) {
                                Text("+", color = TealPrimary, fontSize = 22.sp)
                            }
                        }
                        CampaignField(
                            value = capacity.toString(),
                            label = "Cupos totales de personas",
                            onValueChange = {
                                capacity = it.toIntOrNull()?.coerceAtLeast(1)
                                    ?: capacity
                            }
                        )
                        if (!activityValidationError.isNullOrBlank()) {
                            Text(
                                activityValidationError.orEmpty(),
                                color = BrownPrimary,
                                fontSize = 14.sp
                            )
                        }
                        if (!activityError.isNullOrBlank()) {
                            Text(activityError, color = BrownPrimary, fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                activityValidationError = validateActivity(
                                    activityTitle,
                                    activityDescription,
                                    startsAt,
                                    location,
                                    capacity
                                )
                                if (activityValidationError == null) {
                                    onSaveActivity(
                                        selectedActivity.copy(
                                            title = activityTitle.trim(),
                                            description = activityDescription.trim(),
                                            startsAt = startsAt.trim(),
                                            endsAt = endsAt.trim(),
                                            registrationDeadline =
                                                registrationDeadline.trim(),
                                            location = location.trim(),
                                            capacityPeople = capacity.toString()
                                        ),
                                        activityPhotoUri
                                    )
                                }
                            },
                            enabled = !isSavingActivity,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealPrimary
                            )
                        ) {
                            if (isSavingActivity) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Surface,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.size(8.dp))
                            }
                            Text(
                                if (isSavingActivity) {
                                    "Guardando actividad..."
                                } else {
                                    "Guardar actividad"
                                },
                                color = TextOnPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            "Selecciona una actividad para editar su capacidad.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun EditSectionCard(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        border = BorderStroke(1.dp, Color.White),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon()
                Text(title, color = TealPrimary, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}

@Composable
private fun CampaignField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(18.dp),
        colors = campaignFieldColors()
    )
}

@Composable
private fun campaignFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Surface,
    unfocusedContainerColor = Surface,
    focusedBorderColor = TealPrimary,
    unfocusedBorderColor = Color.Transparent
)

private fun validateActivity(
    title: String,
    description: String,
    startsAt: String,
    location: String,
    capacity: Int
): String? {
    if (title.trim().length < 3) return "El título debe tener al menos 3 caracteres."
    if (description.isBlank()) return "La descripción no puede quedar vacía."
    if (startsAt.isBlank()) return "La fecha de inicio es obligatoria."
    if (location.trim().length < 2) return "La ubicación es obligatoria."
    if (capacity < 1) return "La capacidad debe ser mayor que cero."
    return null
}
