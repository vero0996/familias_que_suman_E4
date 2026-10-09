package mx.tec.familias.ui.screens.mensajes

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class MensajeChat(
    val id: Int,
    val texto: String,
    val hora: String,
    val enviadoPorUsuario: Boolean
)

@Composable
fun ChatScreen(
    conversacion: Conversacion = Conversacion(
        id = 1,
        nombre = "Familia López",
        ultimoMensaje = "",
        hora = ""
    ),
    onBackClick: () -> Unit = {}
) {
    ChatMessages(
        conversacion = conversacion,
        onBackClick = onBackClick
    )
}

@Composable
fun ChatMessages(
    conversacion: Conversacion,
    onBackClick: () -> Unit = {}
) {
    val mensajes = remember {
        mutableStateListOf(
            MensajeChat(
                id = 1,
                texto = "Hola, tenemos una actualización sobre tu actividad.",
                hora = "10:32 AM",
                enviadoPorUsuario = false
            ),
            MensajeChat(
                id = 2,
                texto = "¡Hola! Claro, ¿qué pasó?",
                hora = "10:35 AM",
                enviadoPorUsuario = true
            ),
            MensajeChat(
                id = 3,
                texto = "Se modificó ligeramente el horario de la actividad.",
                hora = "10:37 AM",
                enviadoPorUsuario = false
            )
        )
    }

    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
            .statusBarsPadding()
    ) {

        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.88f)
            ),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.90f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                TealLight.copy(alpha = 0.55f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                TealLight.copy(alpha = 0.65f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = conversacion.nombre,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "En línea",
                            fontSize = 12.sp,
                            color = TealPrimary
                        )
                    }
                }
            }
        }

        // Mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = false
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(
                items = mensajes,
                key = { it.id }
            ) { mensajeActual ->
                MessageBubble(
                    mensaje = mensajeActual
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Campo para escribir
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.90f)
            ),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
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

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (mensaje.isNotBlank()) {
                            mensajes.add(
                                MensajeChat(
                                    id = (mensajes.maxOfOrNull { it.id } ?: 0) + 1,
                                    texto = mensaje.trim(),
                                    hora = "Ahora",
                                    enviadoPorUsuario = true
                                )
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
    mensaje: MensajeChat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.enviadoPorUsuario) {
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
                bottomStart = if (mensaje.enviadoPorUsuario) 22.dp else 6.dp,
                bottomEnd = if (mensaje.enviadoPorUsuario) 6.dp else 22.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (mensaje.enviadoPorUsuario) {
                    TealPrimary
                } else {
                    Surface.copy(alpha = 0.90f)
                }
            ),
            border = if (mensaje.enviadoPorUsuario) {
                null
            } else {
                BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.95f)
                )
            },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                Text(
                    text = mensaje.texto,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (mensaje.enviadoPorUsuario) {
                        Color.White
                    } else {
                        TextPrimary
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mensaje.hora,
                    fontSize = 11.sp,
                    color = if (mensaje.enviadoPorUsuario) {
                        TealLight
                    } else {
                        TextSecondary
                    }
                )
            }
        }
    }
}