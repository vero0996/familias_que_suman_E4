package mx.tec.familias.ui.screens.mensajes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.data.remote.MessageResponse
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.MessageViewModel
import mx.tec.familias.viewmodel.MessageViewModelFactory

@Composable
fun ChatScreen(
    conversacion: Conversacion = Conversacion(
        id = 1,
        nombre = "Familia López",
        ultimoMensaje = "",
        hora = ""
    ),
    activityId: Int? = null,
    eventId: Int? = null,
    workshopId: Int? = null,
    onBackClick: () -> Unit = {}
) {
    ChatMessages(
        conversacion = conversacion,
        activityId = activityId,
        eventId = eventId,
        workshopId = workshopId,
        onBackClick = onBackClick
    )
}

@Composable
fun ChatMessages(
    conversacion: Conversacion,
    activityId: Int? = null,
    eventId: Int? = null,
    workshopId: Int? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val factory = remember {
        MessageViewModelFactory(SessionManager(context))
    }

    val messageViewModel: MessageViewModel = viewModel(factory = factory)
    val mensajes = messageViewModel.mensajes
    val usuarioActualId = messageViewModel.usuarioActualId
    var mensaje by remember { mutableStateOf("") }

    LaunchedEffect(conversacion.id, activityId, eventId, workshopId) {
        when {
            eventId != null -> {
                messageViewModel.cargarMensajesEvento(
                    eventId = eventId,
                    userId = conversacion.id
                )
            }

            workshopId != null -> {
                messageViewModel.cargarMensajesTaller(
                    workshopId = workshopId,
                    userId = conversacion.id
                )
            }

            activityId != null -> {
                messageViewModel.cargarMensajesActividad(
                    activityId = activityId,
                    userId = conversacion.id
                )
            }

            else -> {
                messageViewModel.cargarMensajes(
                    userId = conversacion.id
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
            .statusBarsPadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }

                Spacer(Modifier.width(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(TealLight.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = conversacion.nombre,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = when {
                                eventId != null -> "Chat de evento"
                                workshopId != null -> "Chat de taller"
                                activityId != null -> "Chat de actividad"
                                else -> "En línea"
                            },
                            fontSize = 12.sp,
                            color = TealPrimary
                        )
                    }
                }
            }
        }

        if (messageViewModel.cargando) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                reverseLayout = false
            ) {
                item {
                    Spacer(Modifier.height(14.dp))
                }

                items(
                    items = mensajes,
                    key = { it.id }
                ) { mensajeActual ->
                    MessageBubble(
                        mensaje = mensajeActual,
                        usuarioActualId = usuarioActualId
                    )
                }

                item {
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        if (messageViewModel.error != null) {
            Text(
                text = messageViewModel.error!!,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Escribe un mensaje...",
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TealLight.copy(alpha = 0.20f),
                        unfocusedContainerColor = TealLight.copy(alpha = 0.20f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (mensaje.isNotBlank()) {
                            messageViewModel.enviarMensaje(
                                userId = conversacion.id,
                                texto = mensaje,
                                activityId = activityId,
                                eventId = eventId,
                                workshopId = workshopId
                            )

                            mensaje = ""
                        }
                    },
                    enabled = mensaje.isNotBlank()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (mensaje.isNotBlank()) {
                                    TealPrimary
                                } else {
                                    TealLight.copy(alpha = 0.55f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar mensaje",
                            tint = if (mensaje.isNotBlank()) {
                                Color.White
                            } else {
                                TealPrimary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    mensaje: MessageResponse,
    usuarioActualId: Int?
) {
    val enviadoPorUsuario = mensaje.sender_user_id == usuarioActualId

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (enviadoPorUsuario) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Card(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp,
                bottomStart = if (enviadoPorUsuario) 22.dp else 6.dp,
                bottomEnd = if (enviadoPorUsuario) 6.dp else 22.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (enviadoPorUsuario) {
                    TealPrimary
                } else {
                    Surface
                }
            ),
            border = if (enviadoPorUsuario) {
                null
            } else {
                BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                Text(
                    text = mensaje.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (enviadoPorUsuario) {
                        Color.White
                    } else {
                        TextPrimary
                    }
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = mensaje.created_at
                        .take(16)
                        .replace("T", " "),
                    fontSize = 11.sp,
                    color = if (enviadoPorUsuario) {
                        TealLight
                    } else {
                        TextSecondary
                    }
                )
            }
        }
    }
}