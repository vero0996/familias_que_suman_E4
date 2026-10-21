package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun MisCampaniasScreen(
    campaigns: List<CampaignResponse> = emptyList(),
    actividades: List<Activity> = emptyList(),
    mostrarActividad: Boolean = true,
    isLoading: Boolean = false,
    error: String? = null,
    onInicioClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onNuevaCampaniaClick: () -> Unit = {},
    onGestionarPublicacionesClick: () -> Unit = {},
    onGestionCampaniaClick: () -> Unit = {},
    onEditarCampaniaClick: (Int) -> Unit = {},
    onReutilizarCampaniaClick: () -> Unit = {},
    onActividadClick: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = { AdminScreenHeader("Mis campañas") },

        bottomBar = {
            AdminBottomNavigationBar(
                currentDestination = AdminDestination.CAMPANIAS,
                onDestinationSelected = { destination ->
                    when (destination) {
                        AdminDestination.INICIO -> onInicioClick()
                        AdminDestination.CAMPANIAS -> Unit
                        AdminDestination.MENSAJES -> onMensajesClick()
                        AdminDestination.CONFIGURACION -> onConfiguracionClick()
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCampaniaClick,
                shape = RoundedCornerShape(22.dp),
                containerColor = TealPrimary,
                contentColor = Surface
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva campaña"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Button(
                onClick = onGestionarPublicacionesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    text = "Editar todas mis publicaciones",
                    color = Surface
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }

                !error.isNullOrBlank() -> {
                    Text(
                        text = error,
                        color = BrownPrimary,
                        fontSize = 14.sp
                    )
                }

                campaigns.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "No hay campañas disponibles",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Cuando exista una campaña publicada aparecerá aquí.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                else -> {
                    campaigns.forEach { campaign ->
                        val actividad = actividades.firstOrNull {
                            it.campaignId == campaign.id
                        }

                        CampaignManagementCard(
                            campaign = campaign,
                            actividad = actividad,
                            mostrarActividad = mostrarActividad,
                            onEditClick = {
                                onEditarCampaniaClick(campaign.id)
                            },
                            onParticipantsClick = onGestionCampaniaClick,
                            onReuseClick = onReutilizarCampaniaClick,
                            onActivityClick = {
                                if (mostrarActividad && actividad != null) {
                                    onActividadClick(actividad.id)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
private fun CampaignManagementCard(
    campaign: CampaignResponse,
    actividad: Activity? = null,
    mostrarActividad: Boolean = true,
    onEditClick: () -> Unit,
    onParticipantsClick: () -> Unit,
    onReuseClick: () -> Unit = {},
    onActivityClick: () -> Unit = {}
) {
    val cuposOcupados = actividad?.personasConfirmadas ?: 0
    val cuposTotales = actividad?.capacidadPersonas ?: 0

    val progreso = if (cuposTotales > 0) {
        cuposOcupados.toFloat() / cuposTotales.toFloat()
    } else {
        0f
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = campaign.title,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TealPrimary
                )

                Box(
                    modifier = Modifier
                        .background(
                            TealLight.copy(alpha = 0.2f),
                            RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = campaign.status.replaceFirstChar { it.uppercase() },
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Organización: ${campaign.organization_name}",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Text(
                text = "Creada: ${campaign.created_at.take(10)}",
                color = TextSecondary,
                fontSize = 14.sp
            )

            if (actividad != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TealPrimary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = actividad.nombre,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = actividad.fecha.ifBlank { "Sin fecha" },
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = actividad.hora.ifBlank { "Sin horario" },
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                if (cuposTotales > 0) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cupos ocupados",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "$cuposOcupados / $cuposTotales",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progreso.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = TealPrimary,
                        trackColor = TealLight.copy(alpha = 0.35f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Esta campaña no tiene una actividad disponible.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Divider
            )

            if (mostrarActividad && actividad != null) {
                Button(
                    onClick = onActivityClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Ver actividad",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TealLight.copy(alpha = 0.60f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar campaña",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onParticipantsClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TealLight.copy(alpha = 0.60f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Participantes",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onReuseClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TealLight.copy(alpha = 0.60f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reutilizar",
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MisCampaniasAdminPreview() {
    FamiliasQueSumanTheme(darkTheme = false) {
        MisCampaniasScreen()
    }
}