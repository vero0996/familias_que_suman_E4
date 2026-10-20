package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.ui.theme.*

@Composable
fun ExplorarScreen(
    onInicioClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: (String) -> Unit = {},
    onActividadClick: (String) -> Unit = {},
    estadoActividades: mx.tec.familias.data.model.EstadoActividades? = null,
    campaigns: List<CampaignResponse> = emptyList(),
    mostrarMensajes: Boolean = false,
    cargandoActividades: Boolean = false,
    errorActividades: String? = null,
    onReintentarActividades: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var filtroActual by remember { mutableStateOf("Todos") }

    val busqueda = searchText.trim().lowercase()

    val mostrarCampanias =
        filtroActual == "Todos" ||
            filtroActual == "Donaciones" ||
            filtroActual == "Campañas"

    val mostrarActividadesFiltro =
        filtroActual == "Todos" ||
            filtroActual == "Voluntariado" ||
            filtroActual == "Eventos" ||
            filtroActual == "Talleres"

    val matchingCampaigns = campaigns.filter {
        mostrarCampanias && (
            busqueda.isBlank() ||
                it.title.lowercase().contains(busqueda) ||
                it.description.lowercase().contains(busqueda) ||
                it.organization_name.lowercase().contains(busqueda)
            )
    }

    val matchingEvents = (estadoActividades?.eventos ?: emptyList()).filter {
        mostrarActividadesFiltro && (
            busqueda.isBlank() ||
                it.nombre.lowercase().contains(busqueda) ||
                it.descripcion.lowercase().contains(busqueda) ||
                it.lugar.lowercase().contains(busqueda)
            )
    }

    val hayResultados = matchingCampaigns.isNotEmpty() || matchingEvents.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                ExplorarFilters(
                    filtroActual = filtroActual,
                    onFiltroChange = { filtroActual = it }
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            if (cargandoActividades) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = TealPrimary
                    )

                    Text(
                        text = "Cargando actividades...",
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            if (!errorActividades.isNullOrBlank()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = errorActividades,
                        fontSize = 14.sp,
                        color = BrownPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onReintentarActividades,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            if (matchingCampaigns.isNotEmpty()) {
                item {
                    Text(
                        text = "Campañas",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(matchingCampaigns.size) { index ->
                    val campania = matchingCampaigns[index]

                    CampaignCard(
                        tag = campania.status.uppercase(),
                        tagColor = TealDark,
                        title = campania.title,
                        organization = campania.organization_name,
                        description = campania.description,
                        progress = 0.75f,
                        progressText = "75%",
                        buttonText = "Ver campaña",
                        onClick = { onCampaniaClick(campania.id.toString()) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (matchingEvents.isNotEmpty()) {
                item {
                    Text(
                        text = "Actividades",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(matchingEvents.size) { index ->
                    val evento = matchingEvents[index]
                    val cupos = evento.capacidadPersonas
                    val ocupados = estadoActividades?.ocupados(evento.id) ?: 0
                    val disponibles = estadoActividades?.disponibles(evento.id) ?: cupos

                    ActivityCard(
                        title = evento.nombre,
                        organization = evento.organizacion,
                        participants = "$ocupados/$cupos personas · $disponibles lugares disponibles",
                        date = evento.fecha,
                        onClick = { onActividadClick(evento.id) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (
                !hayResultados &&
                !cargandoActividades &&
                errorActividades.isNullOrBlank()
            ) {
                item {
                    Text(
                        text = "No encontramos resultados",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplorarFilters(filtroActual: String, onFiltroChange: (String) -> Unit) {
    val scrollState = rememberScrollState()
    val opciones = listOf("Todos", "Voluntariado", "Donaciones", "Campañas", "Eventos", "Talleres")

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
                shape = RoundedCornerShape(22.dp),
                border = null
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.reforestacionurbana),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(22.dp))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, color = TealDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = description, fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(18.dp))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.utilesescolares),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(22.dp))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, color = TealDark)
                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = participants, fontSize = 14.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(text = "Ver actividad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}