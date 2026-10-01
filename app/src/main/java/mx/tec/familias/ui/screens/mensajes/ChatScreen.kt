package mx.tec.familias.ui.screens.mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.ui.theme.Surface

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

    var mensaje by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(TealLight)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
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

        // Mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = false
        ) {

            items(
                items = mensajes,
                key = { it.id }
            ) { mensajeActual ->

                MessageBubble(
                    mensaje = mensajeActual
                )
            }
        }

        // Campo para escribir
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
                    focusedContainerColor = Background,
                    unfocusedContainerColor = Background,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
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
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar mensaje",
                    tint = TealPrimary
                )
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

        Column(
            modifier = Modifier
                .width(280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (mensaje.enviadoPorUsuario) 16.dp else 4.dp,
                        bottomEnd = if (mensaje.enviadoPorUsuario) 4.dp else 16.dp
                    )
                )
                .background(
                    if (mensaje.enviadoPorUsuario) {
                        TealPrimary
                    } else {
                        Surface
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {

            Text(
                text = mensaje.texto,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (mensaje.enviadoPorUsuario) {
                    androidx.compose.ui.graphics.Color.White
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