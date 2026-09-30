package mx.tec.familias.ui.screens.admin.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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

@Composable
fun DashboardAdminScreen(
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCrearCampaniaClick: () -> Unit = {},
    onReutilizarCampaniaClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        // Aquí agregamos la barra de navegación inferior
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, indicatorColor = TealLight)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Campañas") },
                    label = { Text("Campañas") },
                    selected = false,
                    onClick = onCampaniasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
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
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TopBar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(end = 12.dp).size(28.dp)
                )
                Text(
                    text = "CommunityHub",
                    color = TealPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // FOTO DE PERFIL CIRCULAR
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil), // Asegúrate de que el archivo se llame así
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop, // Esto evita que se aplaste
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape) // Esto la recorta en círculo perfecto
                        .clickable {
                            onPerfilClick()
                        }
                )
            }

            // Contenido con scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                // Saludo
                Column(modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()) {
                    Text(
                        text = "Hola, Asociación Banco\nde Alimentos",
                        color = TealPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Aquí tienes el resumen de tu impacto\nhoy.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Alerta Necesidades Urgentes
                Row(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .background(WarningBackground, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = Error,
                        modifier = Modifier.padding(end = 8.dp).size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Necesidades Urgentes",
                            color = Error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Faltan 5 voluntarios para la\ncampaña \"Recogida de Invierno\" de\neste fin de semana.",
                            color = Error,
                            fontSize = 14.sp
                        )
                    }
                }

                // Botón Crear Campaña (con ícono integrado)
                Button(
                    onClick = onCrearCampaniaClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(80.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Crear", tint = TextOnPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Crear campaña", color = TextOnPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Botón Reutilizar (con ícono integrado)
                OutlinedButton(
                    onClick = onReutilizarCampaniaClick,
                    border = BorderStroke(1.dp, TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(80.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = "Reutilizar", tint = TealPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Reutilizar campaña anterior", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Resumen de Impacto
                Text(
                    text = "Resumen de Impacto",
                    color = TealPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    // Tarjeta 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .border(1.dp, Divider, RoundedCornerShape(12.dp))
                            .background(Surface, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = "Campañas", tint = TealPrimary)
                        Text("3", color = TealPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text("CAMPAÑAS ACTIVAS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Tarjeta 2
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .border(1.dp, Divider, RoundedCornerShape(12.dp))
                            .background(Surface, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = BrownPrimary)
                        Text("142", color = BrownPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text("PARTICIPANTES TOTALES", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Próximas Actividades
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Próximas Actividades", color = TealPrimary, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Ver todas",
                        color = TealPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onCampaniasClick()
                        }
                    )
                }

                // Tarjeta Actividad 1 (Borde Rojo)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(Error))
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Recogida de Invierno - Centro...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(modifier = Modifier.background(WarningBackground, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text("URGENTE", color = Error, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(12.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sáb, 15 Nov • 10:00 AM", fontSize = 12.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Lugares disponibles", fontSize = 12.sp, color = TextSecondary)
                                Text("2/20 cupos", fontSize = 12.sp, color = Error, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(progress = { 0.9f }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = Error, trackColor = Divider)
                        }
                    }
                }

                // Tarjeta Actividad 2 (Borde Teal)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(TealLight))
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Taller de Sensibilización Escolar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(12.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mar, 19 Nov • 09:30 AM", fontSize = 12.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Lugares disponibles", fontSize = 12.sp, color = TextSecondary)
                                Text("12/15 cupos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(progress = { 0.2f }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = TealLight, trackColor = Divider)
                        }
                    }
                }
            }
        }
    }
}