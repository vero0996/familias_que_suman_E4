package mx.tec.familias.ui.screens.admin.mensajes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesAdminScreen(
    onInicioClick: () -> Unit = {},
    onCampaniasClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mensajes", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.fotoperfil), // Tu foto de perfil
                        contentDescription = "Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                onPerfilClick()
                            }
                    )
                },
                actions = {
                    IconButton(onClick = { }, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            // Barra de navegación inferior corregida según tu equipo
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
                    selected = false, // <-- Corregido: Ya no está seleccionado
                    onClick = onCampaniasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = true, // <-- Corregido: Ahora Mensajes es el activo
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, indicatorColor = TealLight)
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
        // Usamos LazyColumn porque es una lista de chats que puede crecer
        // Usamos LazyColumn porque es una lista de chats que puede crecer
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 50.dp, bottom = 32.dp)
        ) {
            item {
                MensajeCard(
                    titulo = "Colecta de útiles esc...",
                    remitente = "Fundación Aprender Juntos",
                    mensaje = "¡Hola! No olviden traer las m...",
                    hora = "10:30",
                    noLeidos = 1,
                    esAnuncio = true,
                    imagenId = R.drawable.utilesescolares, // <-- TU FOTO 1 AQUÍ
                    onClick = onChatClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                MensajeCard(
                    titulo = "Apoyo al comedor comun...",
                    remitente = "Red Solidaria",
                    mensaje = "Se ha confirmado el punto de encuentro.",
                    hora = "Ayer",
                    noLeidos = 0,
                    esAnuncio = false,
                    imagenId = R.drawable.comedorcomunitario, // <-- TU FOTO 2 AQUÍ
                    onClick = onChatClick
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                MensajeCard(
                    titulo = "Jornada familiar de volur...",
                    remitente = "Asociación EcoVerde",
                    mensaje = "Gracias por participar en la reforestación.",
                    hora = "Lunes",
                    noLeidos = 0,
                    esAnuncio = false,
                    imagenId = R.drawable.eco, // <-- TU FOTO 3 AQUÍ
                    onClick = onChatClick
                )
            }
        }
    }
}

// Componente reutilizable para cada chat de la lista
@Composable
fun MensajeCard(
    titulo: String,
    remitente: String,
    mensaje: String,
    hora: String,
    noLeidos: Int,
    esAnuncio: Boolean,
    imagenId: Int,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del chat
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Divider)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido de texto
            Column(modifier = Modifier.weight(1f)) {
                // Fila: Título y Hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titulo,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(
                        text = hora,
                        color = if (noLeidos > 0) TealPrimary else TextSecondary,
                        fontWeight = if (noLeidos > 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Remitente
                Text(
                    text = remitente,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Fila: Mensaje (con megáfono si aplica) y Globo de no leídos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (esAnuncio) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = BrownPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = mensaje,
                            color = if (noLeidos > 0) TealPrimary else TextSecondary,
                            fontWeight = if (noLeidos > 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (noLeidos > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(20.dp).clip(CircleShape).background(BrownPrimary)
                        ) {
                            Text(
                                text = noLeidos.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}