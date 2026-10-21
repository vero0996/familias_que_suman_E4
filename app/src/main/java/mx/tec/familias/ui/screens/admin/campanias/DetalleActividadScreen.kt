package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Error
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.EditablePublication

@Composable
fun DetalleActividadScreen(
    publication: EditablePublication?,
    isLoading: Boolean,
    isCancelling: Boolean,
    error: String?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onManageParticipants: () -> Unit,
    onCancelActivity: () -> Unit,
    onShare: () -> Unit
) {
    var showCancelConfirmation by remember { mutableStateOf(false) }
    val capacity = publication?.capacityPeople?.toIntOrNull() ?: 0
    val confirmedPeople = publication?.confirmedPeople ?: 0
    val occupancy = if (capacity > 0) {
        (confirmedPeople.toFloat() / capacity).coerceIn(0f, 1f)
    } else {
        0f
    }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text("Cancelar actividad") },
            text = {
                Text("La actividad se marcará como cancelada y se conservará su historial.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                        onCancelActivity()
                    },
                    enabled = !isCancelling
                ) {
                    Text("Cancelar actividad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Detalle de actividad",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TealPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onShare,
                        enabled = publication != null
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir actividad",
                            tint = TealPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading && publication == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealPrimary)
            }

            publication == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                DetailCard {
                    Text(
                        error ?: "No se encontró la actividad.",
                        color = if (error.isNullOrBlank()) TextSecondary else Error
                    )
                }
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DetailCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = publication.title,
                                modifier = Modifier.weight(1f),
                                fontSize = 24.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                            Spacer(Modifier.padding(start = 8.dp))
                            StatusBadge(publication.status)
                        }

                        if (publication.description.isNotBlank()) {
                            Text(
                                text = publication.description,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Text(
                    text = "Información de la actividad",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                DetailCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null
                                )
                            },
                            label = "Fecha de inicio",
                            value = publication.startsAt.toDisplayDate()
                        )
                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null
                                )
                            },
                            label = "Fecha de término",
                            value = publication.endsAt.toDisplayDate()
                        )
                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null
                                )
                            },
                            label = "Límite de inscripción",
                            value = publication.registrationDeadline.toDisplayDate()
                        )
                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null
                                )
                            },
                            label = "Ubicación",
                            value = publication.location
                        )
                    }
                }

                DetailCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                                Text(
                                    text = "Participantes",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "$confirmedPeople / $capacity",
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { occupancy },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = TealPrimary,
                            trackColor = TealLight.copy(alpha = 0.45f)
                        )
                        Text(
                            text = if (capacity > 0) {
                                "Lugares disponibles: ${(capacity - confirmedPeople).coerceAtLeast(0)}"
                            } else {
                                "Capacidad no especificada"
                            },
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                publication.campaignId?.let { campaignId ->
                    DetailCard {
                        ActivityDetailRow(
                            label = "Campaña asociada",
                            value = "Campaña #$campaignId"
                        )
                    }
                }

                if (!error.isNullOrBlank()) {
                    DetailCard {
                        Text(error, color = Error)
                    }
                }

                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(
                        "Editar actividad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onManageParticipants,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, TealPrimary)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null)
                        Text("Gestionar participantes", fontWeight = FontWeight.SemiBold)
                    }
                }

                OutlinedButton(
                    onClick = { showCancelConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    enabled = !isCancelling && !publication.status.equals(
                        "cancelada",
                        ignoreCase = true
                    ) && !publication.status.equals("finalizada", ignoreCase = true),
                    border = BorderStroke(1.dp, Error)
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = Error
                        )
                    } else {
                        Text("Cancelar actividad", color = Error)
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val normalized = status.replaceFirstChar { it.uppercase() }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(TealLight.copy(alpha = 0.45f))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = normalized.ifBlank { "Sin estado" },
            color = if (status.equals("cancelada", ignoreCase = true)) {
                BrownPrimary
            } else {
                TealPrimary
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ActivityDetailRow(
    label: String,
    value: String?,
    icon: @Composable (() -> Unit)? = null
) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()?.let {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(TealLight.copy(alpha = 0.4f))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    it
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun String.toDisplayDate(): String =
    replace("T", " ").take(16).ifBlank { "" }
