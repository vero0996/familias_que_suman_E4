package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.Image // Importante para las fotos
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import mx.tec.familias.ui.components.AdaptiveContainer
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
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
import androidx.compose.ui.layout.ContentScale // Para adaptar la foto
import androidx.compose.ui.res.painterResource // Para cargar la foto
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R // Importa tus recursos de Android
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
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

    // AdaptiveContainer(modifier = Modifier.background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),

                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 32.dp
                )
            ) {

                item {
                    ExplorarHeader(
                        onProfileClick = onPerfilClick
                    )
                }

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

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
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Le pasamos el filtro actual y la función para cambiarlo
                        ExplorarFilters(
                            filtroActual = filtroActual,
                            onFiltroChange = { nuevoFiltro -> filtroActual = nuevoFiltro }
                        )

                        Spacer(modifier = Modifier.height(30.dp))

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
                            Spacer(modifier = Modifier.height(18.dp))

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

                        if (filtroActual == "Todos" || filtroActual == "Voluntariado" || filtroActual == "Eventos") {
                            ActivityCard(
                                title = "Plantación de Árboles en El Pardo",
                                organization = "Asociación Bosque Vivo",
                                participants = estadoActividades?.let { "${it.ocupados("arboles")}/${it.evento("arboles").capacidadFamilias} familias · ${it.disponibles("arboles")} lugares disponibles" } ?: "Actividad familiar",
                                date = "24 noviembre 2026",
                                onClick = { onActividadClick("arboles") }
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            ActivityCard(
                                title = "Lectura Compartida",
                                organization = "Fundación Aprender Juntos",
                                participants = estadoActividades?.let { "${it.ocupados("lectura")}/${it.evento("lectura").capacidadFamilias} familias · ${it.disponibles("lectura")} lugares disponibles" } ?: "Actividad familiar",
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
//}

@Composable
private fun ExplorarHeader(
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = TealPrimary
            )
        }

        Text(
            text = "Familias que Suman +",
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )

        IconButton(onClick = onProfileClick) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(TealDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
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
                label = { Text(text = opcion) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OrangePrimary,
                    selectedLabelColor = TextPrimary,
                    containerColor = Surface,
                    labelColor = TextPrimary
                ),
                shape = RoundedCornerShape(50)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Imagen Real en lugar de Box gris
            Image(
                painter = painterResource(id = R.drawable.reforestacionurbana), // Cambia 'icon' por tu imagen
                contentDescription = "Imagen de Campaña",
                contentScale = ContentScale.Crop, // Esto hace que la imagen se adapte bien
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TealDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = description, fontSize = 14.sp, lineHeight = 21.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Progreso", fontSize = 12.sp, color = TextSecondary)
                    Text(text = progressText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }

                Spacer(modifier = Modifier.height(7.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = TealPrimary,
                    trackColor = Divider
                )
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Imagen Real en lugar de Box gris
            Image(
                painter = painterResource(id = R.drawable.utilesescolares), // Cambia 'icon' por tu imagen
                contentDescription = "Imagen de Actividad",
                contentScale = ContentScale.Crop, // Adapta la imagen
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BrownPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(7.dp))
                Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(text = title, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = organization, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TealDark)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(7.dp))
                Text(text = participants, fontSize = 14.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(text = "Ver actividad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}