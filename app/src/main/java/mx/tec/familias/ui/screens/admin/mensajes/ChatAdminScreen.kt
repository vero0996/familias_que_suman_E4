package mx.tec.familias.ui.screens.admin.mensajes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.*
import mx.tec.familias.R
import mx.tec.familias.data.repository.ChatRepository
import mx.tec.familias.ui.screens.mensajes.MensajeChat
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAdminScreen(
    nombreChat: String = "Conversación",
    imagenId: Int = R.drawable.icon, // <-- Recibe el ID de la foto aquí
    onBackClick: () -> Unit = {}
) {
    var textoMensaje by remember { mutableStateOf("") }

    val listaEstado = rememberLazyListState()
    val alcance = rememberCoroutineScope()

    val conversacion = ChatRepository.conversaciones.firstOrNull { it.nombre == nombreChat } ?: ChatRepository.conversaciones.first()
    val mensajes = ChatRepository.getMensajes(conversacion.id)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Círculo con la FOTO REAL del chat
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = imagenId), // <-- Pintamos la foto real
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = nombreChat,
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Activo recientemente",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            Surface(
                color = Surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textoMensaje,
                        onValueChange = { textoMensaje = it },
                        placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (textoMensaje.isNotBlank()) {
                                ChatRepository.enviarMensaje(
                                    conversacion.id,
                                    textoMensaje.trim(),
                                    enviadoPorUsuario = false
                                )

                                textoMensaje = ""

                                alcance.launch {
                                    if (mensajes.isNotEmpty()) {
                                        listaEstado.animateScrollToItem(
                                            mensajes.lastIndex
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(TealPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    )
    { paddingValues ->
        LazyColumn(
            state = listaEstado,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(mensajes) { mensaje ->
                val esDelAdmin = !mensaje.enviadoPorUsuario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (esDelAdmin) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp,
                            bottomStart = if (esDelAdmin) 16.dp else 0.dp,
                            bottomEnd = if (esDelAdmin) 0.dp else 16.dp
                        ),
                        color = if (esDelAdmin) TealPrimary else Surface,
                        modifier = Modifier.widthIn(max = 280.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = mensaje.texto, color = if (esDelAdmin) Color.White else TextPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = mensaje.hora, color = if (esDelAdmin) Color.White.copy(alpha = 0.7f) else TextSecondary, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
                        }
                    }
                }
            }
        }
    }
}