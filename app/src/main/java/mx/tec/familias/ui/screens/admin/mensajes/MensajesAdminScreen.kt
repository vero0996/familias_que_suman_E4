package mx.tec.familias.ui.screens.admin.mensajes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.R
import mx.tec.familias.data.model.ParticipanteActividadAdmin
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.*
import mx.tec.familias.viewmodel.MessageViewModel
import mx.tec.familias.viewmodel.MessageViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesAdminScreen(
    onInicioClick: () -> Unit = {},
    onCampaniasClick: () -> Unit = {},
    onChatClick: (Int, String, Int) -> Unit = { _, _, _ -> },
    onConfiguracionClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    participantes: List<ParticipanteActividadAdmin> = emptyList(),
) {
    var mostrarFormulario by remember { mutableStateOf(false) }
    var asuntoMensaje by remember { mutableStateOf("") }
    var contenidoMensaje by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var mensajeEnviado by remember { mutableStateOf(false) }
    var mostrarParticipantes by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val factory = remember { MessageViewModelFactory(SessionManager(context)) }
    val messageViewModel: MessageViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        messageViewModel.cargarUsuarios()
    }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Mensajes",
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.fotoperfil),
                        contentDescription = "Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(start = 16.dp).size(36.dp).clip(CircleShape).clickable { onPerfilClick() }
                    )
                },
                actions = {
                    IconButton(onClick = { }, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = TealPrimary)
                    }
                }
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
                    selected = false,
                    onClick = onCampaniasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = true,
                    onClick = {},
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 50.dp, bottom = 32.dp)
        ) {
            item {
                Button(
                    onClick = {
                        mostrarFormulario = true
                        mensajeEnviado = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Group, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Enviar mensaje a todos", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(onClick = { mostrarParticipantes = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Contactar individualmente")
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (messageViewModel.cargando) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (messageViewModel.error != null) {
                item {
                    Text(
                        text = messageViewModel.error ?: "No se pudieron cargar los usuarios.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                items(
                    items = messageViewModel.usuarios,
                    key = { it.id }
                ) { usuario ->
                    MensajeCard(
                        titulo = usuario.full_name,
                        remitente = "Familia",
                        mensaje = "Abrir conversación",
                        hora = "",
                        noLeidos = 0,
                        esAnuncio = false,
                        imagenId = R.drawable.icon,
                        onClick = { onChatClick(usuario.id, usuario.full_name, R.drawable.icon) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = {
                Text(text = "Mensaje para todos", color = TealPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "El mensaje se preparará para todos los participantes de la campaña.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = asuntoMensaje,
                        onValueChange = { asuntoMensaje = it },
                        label = { Text("Asunto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = contenidoMensaje,
                        onValueChange = { contenidoMensaje = it },
                        label = { Text("Escribe tu mensaje") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarFormulario = false
                        mostrarConfirmacion = true
                    },
                    enabled = asuntoMensaje.isNotBlank() && contenidoMensaje.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary)
                ) {
                    Text("Revisar envío")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = Surface
        )
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = {
                Text(
                    text = if (mensajeEnviado) "¡Envío simulado!" else "Confirmar mensaje grupal",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (mensajeEnviado) {
                            "La simulación terminó. No se enviaron mensajes reales."
                        } else {
                            "Se preparará el mensaje para todos los participantes."
                        },
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = asuntoMensaje, color = TealPrimary, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = contenidoMensaje, color = TextPrimary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mensajeEnviado) {
                            mostrarConfirmacion = false
                            asuntoMensaje = ""
                            contenidoMensaje = ""
                            mensajeEnviado = false
                        } else {
                            mensajeEnviado = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary)
                ) {
                    Text(if (mensajeEnviado) "Aceptar" else "Confirmar envío")
                }
            },
            dismissButton = {
                if (!mensajeEnviado) {
                    TextButton(
                        onClick = {
                            mostrarConfirmacion = false
                            mostrarFormulario = true
                        }
                    ) {
                        Text("Volver a editar", color = TealPrimary)
                    }
                }
            },
            containerColor = Surface
        )
    }

    if (mostrarParticipantes) {
        AlertDialog(
            onDismissRequest = { mostrarParticipantes = false },
            title = { Text("Contactar participante") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona a quién deseas escribirle:")

                    messageViewModel.usuarios.forEach { usuario ->
                        OutlinedButton(
                            onClick = {
                                mostrarParticipantes = false
                                onChatClick(usuario.id, usuario.full_name, R.drawable.icon)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(usuario.full_name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarParticipantes = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

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
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(TealLight)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titulo,
                        color = TextPrimary,
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

                Text(
                    text = remitente,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (esAnuncio) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = BrownPrimary,
                                modifier = Modifier.size(16.dp)
                            )
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

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Preview(showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
private fun MensajesAdminScreenPreview() {
    FamiliasQueSumanTheme(darkTheme = false) {
        MensajesAdminScreen(participantes = emptyList())
    }
}