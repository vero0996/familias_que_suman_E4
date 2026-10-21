package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.AdminPublicationsUiState
import mx.tec.familias.viewmodel.EditablePublication
import mx.tec.familias.viewmodel.PublicationKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPublicacionesScreen(
    state: AdminPublicationsUiState,
    onBackClick: () -> Unit,
    onEditClick: (EditablePublication) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Mis publicaciones",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edita los datos que se guardan para cada tipo de publicación.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            if (state.isLoading && state.campaigns.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TealPrimary
                )
            }

            if (!state.error.isNullOrBlank()) {
                Text(state.error, color = BrownPrimary)
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Reintentar")
                }
            }

            PublicationSection("Campañas", state.campaigns, onEditClick)
            PublicationSection("Actividades", state.activities, onEditClick)
            PublicationSection("Proyectos", state.projects, onEditClick)
            PublicationSection("Eventos", state.events, onEditClick)
            PublicationSection("Talleres", state.workshops, onEditClick)
            PublicationSection("Donaciones", state.donations, onEditClick)
        }
    }
}

@Composable
private fun PublicationSection(
    title: String,
    publications: List<EditablePublication>,
    onEditClick: (EditablePublication) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            color = TealPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (publications.isEmpty()) {
            Text("No hay publicaciones disponibles.", color = TextSecondary)
        } else {
            publications.forEach { publication ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditClick(publication) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                publication.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                publication.secondaryText(),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar ${publication.title}",
                            tint = TealPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun EditablePublication.secondaryText(): String =
    when (kind) {
        PublicationKind.CAMPAIGN -> organizationName
        PublicationKind.ACTIVITY -> listOf(startsAt, "$capacityPeople personas")
            .filter(String::isNotBlank)
            .joinToString(" • ")
        PublicationKind.PROJECT -> description
        PublicationKind.EVENT, PublicationKind.WORKSHOP ->
            listOf(startsAt, location).filter(String::isNotBlank).joinToString(" • ")
        PublicationKind.DONATION -> "Meta: $goalAmount"
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPublicacionScreen(
    publication: EditablePublication?,
    isSaving: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onSave: (EditablePublication) -> Unit
) {
    if (publication == null) {
        Scaffold(
            containerColor = TealLight.copy(alpha = 0.12f),
            topBar = { AdminScreenHeader(title = "Editar publicación") }
        ) { padding ->
            Text(
                text = "No se encontró la publicación. Regresa y vuelve a cargar la lista.",
                modifier = Modifier.padding(padding).padding(20.dp),
                color = BrownPrimary
            )
        }
        return
    }

    var title by remember(publication) { mutableStateOf(publication.title) }
    var description by remember(publication) { mutableStateOf(publication.description) }
    var organizationName by remember(publication) {
        mutableStateOf(publication.organizationName)
    }
    var location by remember(publication) { mutableStateOf(publication.location) }
    var startsAt by remember(publication) { mutableStateOf(publication.startsAt) }
    var endsAt by remember(publication) { mutableStateOf(publication.endsAt) }
    var registrationDeadline by remember(publication) {
        mutableStateOf(publication.registrationDeadline)
    }
    var capacity by remember(publication) { mutableStateOf(publication.capacityPeople) }
    var goalAmount by remember(publication) { mutableStateOf(publication.goalAmount) }
    var currentAmount by remember(publication) { mutableStateOf(publication.currentAmount) }
    var validationError by remember(publication) { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Editar ${publication.kind.label()}",
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (publication.kind == PublicationKind.CAMPAIGN) {
                OutlinedTextField(
                    value = organizationName,
                    onValueChange = { organizationName = it },
                    label = { Text("Nombre de la organización") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (publication.kind in setOf(
                    PublicationKind.ACTIVITY,
                    PublicationKind.EVENT,
                    PublicationKind.WORKSHOP
                )
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                DateTimeField(
                    value = startsAt,
                    label = "Fecha y hora de inicio",
                    onValueChange = { startsAt = it }
                )
            }

            if (publication.kind == PublicationKind.ACTIVITY) {
                DateTimeField(
                    value = endsAt,
                    label = "Fecha y hora de fin (opcional)",
                    onValueChange = { endsAt = it }
                )
                DateTimeField(
                    value = registrationDeadline,
                    label = "Fecha límite de inscripción",
                    onValueChange = { registrationDeadline = it }
                )
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacidad de personas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (publication.kind == PublicationKind.DONATION) {
                OutlinedTextField(
                    value = goalAmount,
                    onValueChange = { goalAmount = it },
                    label = { Text("Meta de donación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = currentAmount,
                    onValueChange = { currentAmount = it },
                    label = { Text("Monto recaudado") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Text(
                text = "Las fechas se envían en formato ISO 8601, por ejemplo: 2026-10-20T09:00:00-06:00.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            validationError?.let { Text(it, color = BrownPrimary) }
            error?.let { Text(it, color = BrownPrimary) }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val errorMessage = validateDraft(
                        publication.kind,
                        title,
                        description,
                        organizationName,
                        location,
                        startsAt,
                        capacity,
                        goalAmount,
                        currentAmount
                    )
                    validationError = errorMessage
                    if (errorMessage == null) {
                        onSave(
                            publication.copy(
                                title = title.trim(),
                                description = description.trim(),
                                organizationName = organizationName.trim(),
                                location = location.trim(),
                                startsAt = startsAt.trim(),
                                endsAt = endsAt.trim(),
                                registrationDeadline = registrationDeadline.trim(),
                                capacityPeople = capacity.trim(),
                                goalAmount = goalAmount.trim(),
                                currentAmount = currentAmount.trim()
                            )
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Surface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios", color = Surface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DateTimeField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("AAAA-MM-DDThh:mm:ss-06:00") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

private fun validateDraft(
    kind: PublicationKind,
    title: String,
    description: String,
    organizationName: String,
    location: String,
    startsAt: String,
    capacity: String,
    goalAmount: String,
    currentAmount: String
): String? {
    if (title.trim().length < 3) return "El título debe tener al menos 3 caracteres."
    if (description.isBlank()) return "La descripción no puede quedar vacía."
    if (kind == PublicationKind.CAMPAIGN && organizationName.trim().length < 2) {
        return "El nombre de la organización es obligatorio."
    }
    if (kind in setOf(PublicationKind.ACTIVITY, PublicationKind.EVENT, PublicationKind.WORKSHOP)) {
        if (location.trim().length < 2) return "La ubicación debe tener al menos 2 caracteres."
        if (startsAt.isBlank()) return "La fecha de inicio es obligatoria."
    }
    if (kind == PublicationKind.ACTIVITY && capacity.toIntOrNull()?.let { it > 0 } != true) {
        return "La capacidad debe ser un número mayor que cero."
    }
    if (kind == PublicationKind.DONATION) {
        if (goalAmount.toIntOrNull()?.let { it > 0 } != true) {
            return "La meta debe ser un número mayor que cero."
        }
        if (currentAmount.toIntOrNull()?.let { it >= 0 } != true) {
            return "El monto recaudado debe ser cero o mayor."
        }
    }
    return null
}

private fun PublicationKind.label(): String = when (this) {
    PublicationKind.CAMPAIGN -> "campaña"
    PublicationKind.ACTIVITY -> "actividad"
    PublicationKind.PROJECT -> "proyecto"
    PublicationKind.EVENT -> "evento"
    PublicationKind.WORKSHOP -> "taller"
    PublicationKind.DONATION -> "donación"
}
