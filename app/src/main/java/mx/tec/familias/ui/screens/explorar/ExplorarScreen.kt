package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun ExplorarScreen(
    onInicioClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: () -> Unit = {},
    onActividadClick: (String) -> Unit = {},
    estadoActividades: mx.tec.familias.data.model.EstadoActividades? = null,
    mostrarMensajes: Boolean = false
) {
    var searchText by remember { mutableStateOf("") }

    // Estado para saber qué filtro está seleccionado
    var filtroActual by remember { mutableStateOf("Todos") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp)
        ) {
            item {
                ExplorarHeader(onProfileClick = onPerfilClick)
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Explorar",
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        placeholder = { Text(text = "Buscar causas, asociaciones...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = TealPrimary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.86f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.90f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Le pasamos el filtro actual y la función para cambiarlo
                    ExplorarFilters(
                        filtroActual = filtroActual,
                        onFiltroChange = { nuevoFiltro -> filtroActual = nuevoFiltro }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Campañas",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar campañas dinámicamente según el filtro
                    if (filtroActual == "Todos" || filtroActual == "Donaciones") {
                        CampaignCard(
                            tag = "URGENTE",
                            tagColor = BrownPrimary,
                            title = "Útiles Escolares para Todos",
                            organization = "Fundación Aprender Juntos",
                            description = "Ayuda a que niñas y niños comiencen el ciclo escolar con todo lo necesario.",
                            progress = 0.80f,
                            progressText = "80%",
                            buttonText = "Ver campaña",
                            onClick = onCampaniaClick
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        CampaignCard(
                            tag = "DONACIÓN",
                            tagColor = TealDark,
                            title = "Canasta Solidaria de Invierno",
                            organization = "Asociación Manos Unidas",
                            description = "Apoya a familias de la comunidad con alimentos y productos básicos.",
                            progress = 0.45f,
                            progressText = "45%",
                            buttonText = "Ver campaña",
                            onClick = onCampaniaClick
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    Text(
                        text = "Actividades",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (
                        filtroActual == "Todos" ||
                        filtroActual == "Voluntariado" ||
                        filtroActual == "Eventos"
                    ) {
                        ActivityCard(
                            title = "Plantación de Árboles en El Pardo",
                            organization = "Asociación Bosque Vivo",
                            participants = estadoActividades?.let {
                                "${it.ocupados("arboles")}/${it.evento("arboles").capacidadFamilias} familias · ${it.disponibles("arboles")} lugares disponibles"
                            } ?: "Actividad familiar",
                            date = "24 noviembre 2026",
                            onClick = { onActividadClick("arboles") }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ActivityCard(
                            title = "Lectura Compartida",
                            organization = "Fundación Aprender Juntos",
                            participants = estadoActividades?.let {
                                "${it.ocupados("lectura")}/${it.evento("lectura").capacidadFamilias} familias · ${it.disponibles("lectura")} lugares disponibles"
                            } ?: "Actividad familiar",
                            date = "25 noviembre 2026",
                            onClick = { onActividadClick("lectura") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.EXPLORAR,
            mostrarMensajes = mostrarMensajes,
            onDestinationSelected = { destination ->
                when (destination) {
                    FamilyDestination.INICIO -> onInicioClick()
                    FamilyDestination.EXPLORAR -> Unit
                    FamilyDestination.ACTIVIDADES -> onActividadesClick()
                    FamilyDestination.MENSAJES -> onMensajesClick()
                    FamilyDestination.PERFIL -> onPerfilClick()
                }
            }
        )
    }
}

@Composable
private fun ExplorarHeader(
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TealLight.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = TealPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Text(
                text = "Familias que Suman +",
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )

            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(TealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplorarFilters(
    filtroActual: String,
    onFiltroChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val opciones = listOf("Todos", "Voluntariado", "Donaciones", "Eventos", "Talleres")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            FilterChip(
                selected = filtroActual == opcion,
                onClick = { onFiltroChange(opcion) },
                label = {
                    Text(
                        text = opcion,
                        fontWeight = if (filtroActual == opcion) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = Surface.copy(alpha = 0.84f),
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(22.dp)
            )
        }
    }
}

@Composable
private fun CampaignCard(
    tag: String,
    tagColor: Color,
    title: String,
    organization: String,
    description: String,
    progress: Float,
    progressText: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.reforestacionurbana),
                contentDescription = "Imagen de Campaña",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 14.dp,
                    bottom = 12.dp
                )
            ) {
                Text(
                    text = tag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = organization,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = progressText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = TealPrimary,
                    trackColor = TealLight.copy(alpha = 0.35f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    organization: String,
    participants: String,
    date: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.utilesescolares),
                contentDescription = "Imagen de Actividad",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 14.dp,
                    bottom = 12.dp
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrownPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = organization,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = participants,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(
                        text = "Ver actividad",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}