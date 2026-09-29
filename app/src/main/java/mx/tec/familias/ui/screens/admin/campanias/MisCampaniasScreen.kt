package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.tec.familias.R // Importante para leer tus imágenes
import mx.tec.familias.ui.components.CampaignManagementCard
import mx.tec.familias.ui.theme.*

@Composable
fun MisCampaniasScreen() {
    Scaffold(
        containerColor = Background,
        // Barra de navegación idéntica a la del Dashboard
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Campañas") },
                    label = { Text("Campañas") },
                    selected = true, // Marcamos Campañas como activa
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, indicatorColor = TealLight)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Título y botón de "+"
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Mis Campañas",
                    color = TealPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { /* TODO: Nueva Campaña */ },
                    modifier = Modifier
                        .size(48.dp) // Lo hice un poco más grande según Figma
                        .clip(CircleShape)
                        .background(OrangePrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                }
            }

            // Pestañas (Activas, Próximas, Anteriores)
            TabRow(
                selectedTabIndex = 0,
                containerColor = Background,
                contentColor = TealPrimary,
            ) {
                Tab(selected = true, onClick = { }, text = { Text("Activas", fontWeight = FontWeight.Bold) })
                Tab(selected = false, onClick = { }, text = { Text("Próximas") })
                Tab(selected = false, onClick = { }, text = { Text("Anteriores") })
            }

            // Lista de Tarjetas
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    CampaignManagementCard(
                        titulo = "Recogida de Invierno",
                        fecha = "15 Nov - 30 Nov, 2023",
                        hora = "10:00 - 14:00",
                        cuposOcupados = 12,
                        cuposTotales = 20,
                        estado = "Activa",
                        imagenId = R.drawable.recorridainvierno // Tu nueva foto
                    )
                }

                item {
                    CampaignManagementCard(
                        titulo = "Reforestación Urbana",
                        fecha = "02 Dic, 2023",
                        hora = "09:00 - 13:30",
                        cuposOcupados = 45,
                        cuposTotales = 50,
                        estado = "Activa",
                        imagenId = R.drawable.reforestacionurbana // Tu nueva foto
                    )
                }
            }
        }
    }
}