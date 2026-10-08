package mx.tec.familias.ui.screens.mensajes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.components.TopBar
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class Conversacion(
    val id: Int,
    val nombre: String,
    val ultimoMensaje: String,
    val hora: String,
    val mensajesSinLeer: Int = 0
): java.io.Serializable

@Composable
fun MensajesScreen(
    onInicioClick: () -> Unit = {},
    onExplorarClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onConversacionClick: (Conversacion) -> Unit = {}
) {
    var busqueda by remember {
        mutableStateOf("")
    }

    val conversaciones = remember {
        listOf(
            Conversacion(
                id = 1,
                nombre = "Vida Comunidad",
                ultimoMensaje = "Hola, tenemos una actualización sobre tu actividad.",
                hora = "10:32 AM",
                mensajesSinLeer = 2
            ),
            Conversacion(
                id = 2,
                nombre = "Reforestación Parque Central",
                ultimoMensaje = "¡Gracias por participar! Te esperamos el domingo.",
                hora = "Ayer"
            ),
            Conversacion(
                id = 3,
                nombre = "Centro Comunitario",
                ultimoMensaje = "Recuerda llevar los materiales para el taller.",
                hora = "25 Sep"
            )
        )
    }

    val conversacionesFiltradas = conversaciones.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.ultimoMensaje.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column {
                        TopBar(
                            title = "Mensajes",
                            showProfile = true,
                            onProfileClick = onPerfilClick
                        )

                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                            placeholder = {
                                Text(
                                    text = "Buscar mensajes",
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = TealPrimary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Surface.copy(alpha = 0.92f),
                                unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                                focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.90f)
                            )
                        )
                    }
                }
            }

            if (conversacionesFiltradas.isEmpty()) {
                item {
                    EmptyMessagesCard()
                }
            } else {
                items(
                    items = conversacionesFiltradas,
                    key = { it.id }
                ) { conversacion ->
                    ConversacionCard(
                        conversacion = conversacion,
                        onClick = {
                            onConversacionClick(conversacion)
                        }
                    )
                }
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.MENSAJES,
            mostrarMensajes = true,
            onDestinationSelected = { destination ->
                when (destination) {
                    FamilyDestination.INICIO -> onInicioClick()
                    FamilyDestination.EXPLORAR -> onExplorarClick()
                    FamilyDestination.ACTIVIDADES -> onActividadesClick()
                    FamilyDestination.MENSAJES -> Unit
                    FamilyDestination.PERFIL -> onPerfilClick()
                }
            }
        )
    }
}

@Composable
private fun ConversacionCard(
    conversacion: Conversacion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxAvatar(
                nombre = conversacion.nombre
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversacion.nombre,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Text(
                        text = conversacion.hora,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.padding(top = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversacion.ultimoMensaje,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextSecondary
                    )

                    if (conversacion.mensajesSinLeer > 0) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversacion.mensajesSinLeer.toString(),
                                fontSize = 11.sp,
                                color = Surface,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxAvatar(
    nombre: String
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(TealLight.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = TealPrimary,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun EmptyMessagesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TealLight.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.padding(top = 12.dp))

            Text(
                text = "No encontramos mensajes",
                fontSize = 17.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.padding(top = 6.dp))

            Text(
                text = "Cuando recibas mensajes de organizaciones o actividades aparecerán aquí.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}