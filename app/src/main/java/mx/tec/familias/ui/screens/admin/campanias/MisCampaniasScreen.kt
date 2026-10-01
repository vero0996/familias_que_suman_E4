package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onInicioClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onNuevaCampaniaClick: () -> Unit = {},
    onGestionCampaniaClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Campañas",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onNuevaCampaniaClick) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Campaña", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onInicioClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Campañas") },
                    label = { Text("Campañas") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        indicatorColor = TealLight
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = false,
                    onClick = onMensajesClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = onConfiguracionClick
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCampaniaClick,
                containerColor = TealPrimary,
                contentColor = Surface
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Campaña")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            CampaignManagementCard(
                titulo = "Colecta de Invierno 2026",
                fecha = "15 de Octubre, 2026",
                hora = "10:00 AM - 4:00 PM",
                cuposOcupados = 35,
                cuposTotales = 50,
                estado = "Activa",
                onMensajesClick = onGestionCampaniaClick
            )

            CampaignManagementCard(
                titulo = "Reforestación Comunitaria",
                fecha = "22 de Octubre, 2026",
                hora = "9:00 AM - 1:00 PM",
                cuposOcupados = 20,
                cuposTotales = 30,
                estado = "Próxima",
                onMensajesClick = onGestionCampaniaClick
            )

            Spacer(modifier = Modifier.height(32.dp))
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
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column {
            // Imagen de la campaña
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Portada de campaña",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TealPrimary,
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
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(9999.dp)),
                    color = TealLight,
                    trackColor = Divider
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                // Botones de acción interactivos
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón Editar (Lápiz)
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón Participantes (Personas -> Mensajes)
                    IconButton(
                        onClick = onMensajesClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón Reutilizar (Flecha girando)
                    IconButton(
                        onClick = onReutilizarClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reutilizar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
