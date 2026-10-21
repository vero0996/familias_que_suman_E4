package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCampaniasScreen(
    campaigns: List<mx.tec.familias.data.remote.CampaignResponse> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    mostrarActividad: Boolean = true,
    onInicioClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onNuevaCampaniaClick: () -> Unit = {},
    onGestionarPublicacionesClick: () -> Unit = {},
    onGestionCampaniaClick: () -> Unit = {},
    onEditarCampaniaClick: (Int) -> Unit = {},
    onReutilizarCampaniaClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = { AdminScreenHeader("Mis Campañas") },
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
                Icon(Icons.Default.Add, contentDescription = "Nueva Campaña")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onGestionarPublicacionesClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Editar todas mis publicaciones", color = Surface)
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            } else if (!error.isNullOrBlank()) {
                Text(text = error, color = BrownPrimary, fontSize = 14.sp)
            } else if (campaigns.isNotEmpty()) {
                campaigns.forEach { campaign ->
                    CampaignManagementCard(
                        titulo = campaign.title,
                        fecha = campaign.created_at.take(10),
                        hora = campaign.organization_name,
                        cuposOcupados = 20,
                        cuposTotales = 30,
                        estado = campaign.status.replaceFirstChar { it.uppercase() },
                        imagenId = R.drawable.reforestacionurbana,
                        onEditarClick = { onEditarCampaniaClick(campaign.id) },
                        onMensajesClick = onGestionCampaniaClick,
                        onReutilizarClick = onReutilizarCampaniaClick
                    )
                }
            } else {
                if (mostrarActividad) {
                    CampaignManagementCard(
                        titulo = "Colecta de Invierno 2026",
                        fecha = "15 de Octubre, 2026",
                        hora = "10:00 AM - 4:00 PM",
                        cuposOcupados = 35,
                        cuposTotales = 50,
                        estado = "Activa",
                        imagenId = R.drawable.recorridainvierno,
                        onEditarClick = { onEditarCampaniaClick(0) },
                        onMensajesClick = onGestionCampaniaClick,
                        onReutilizarClick = onReutilizarCampaniaClick
                    )
                }
                CampaignManagementCard(
                    titulo = "Reforestación Comunitaria",
                    fecha = "22 de Octubre, 2026",
                    hora = "9:00 AM - 1:00 PM",
                    cuposOcupados = 20,
                    cuposTotales = 30,
                    estado = "Próxima",
                    imagenId = R.drawable.reforestacionurbana,
                    onEditarClick = { onEditarCampaniaClick(0) },
                    onMensajesClick = onGestionCampaniaClick,
                    onReutilizarClick = onReutilizarCampaniaClick
                )
            }

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
fun CampaignManagementCard(
    titulo: String,
    fecha: String,
    hora: String,
    cuposOcupados: Int,
    cuposTotales: Int,
    estado: String,
    imagenId: Int = R.drawable.icon,
    onEditarClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onReutilizarClick: () -> Unit = {}
) {
    val progreso = cuposOcupados.toFloat() / cuposTotales.toFloat()

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Portada de campaña",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        lineHeight = 26.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(TealLight.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = estado, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = fecha, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = hora, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Text(text = "Cupos ocupados", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        text = cuposOcupados.toString() + " / " + cuposTotales.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),
                    color = TealPrimary,
                    trackColor = TealLight.copy(alpha = 0.35f)
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)).background(TealLight.copy(alpha = 0.60f))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onMensajesClick,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)).background(TealLight.copy(alpha = 0.60f))
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onReutilizarClick,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)).background(TealLight.copy(alpha = 0.60f))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reutilizar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MisCampaniasAdminPreview() {
    FamiliasQueSumanTheme(darkTheme = false) { MisCampaniasScreen() }
}
