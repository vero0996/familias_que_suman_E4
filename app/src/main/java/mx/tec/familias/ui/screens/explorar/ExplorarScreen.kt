package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import mx.tec.familias.R
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.data.model.Evento
import mx.tec.familias.data.model.Taller
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.theme.*

@Composable
fun ExplorarScreen(
    onInicioClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: (String) -> Unit = {},
    onActividadClick: (String) -> Unit = {},
    onEventoClick: (Int) -> Unit = {},
    onTallerClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    estadoActividades: EstadoActividades? = null,
    campaigns: List<CampaignResponse> = emptyList(),
    eventos: List<Evento> = emptyList(),
    talleres: List<Taller> = emptyList(),
    mostrarMensajes: Boolean = false,
    cargandoActividades: Boolean = false,
    errorActividades: String? = null,
    onReintentarActividades: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var filtroActual by remember { mutableStateOf("Todos") }

    val busqueda = searchText.trim().lowercase()

    val mostrarCampanias = filtroActual == "Todos" || filtroActual == "Donaciones" || filtroActual == "Campañas"
    val mostrarActividades = filtroActual == "Todos" || filtroActual == "Voluntariado" || filtroActual == "Eventos"
    val mostrarEventos = filtroActual == "Todos" || filtroActual == "Eventos"
    val mostrarTalleres = filtroActual == "Todos" || filtroActual == "Talleres"

    val matchingCampaigns = campaigns.filter {
        mostrarCampanias && (busqueda.isBlank() ||
                it.title.lowercase().contains(busqueda) ||
                it.description.lowercase().contains(busqueda) ||
                it.organization_name.lowercase().contains(busqueda))
    }

    val matchingActivities = (estadoActividades?.eventos ?: emptyList()).filter {
        mostrarActividades && (busqueda.isBlank() ||
                it.nombre.lowercase().contains(busqueda) ||
                it.descripcion.lowercase().contains(busqueda) ||
                it.lugar.lowercase().contains(busqueda))
    }

    val matchingEventos = eventos.filter {
        mostrarEventos && (busqueda.isBlank() ||
                it.title.lowercase().contains(busqueda) ||
                it.description.lowercase().contains(busqueda) ||
                it.location.lowercase().contains(busqueda))
    }

    val matchingTalleres = talleres.filter {
        mostrarTalleres && (busqueda.isBlank() ||
                it.title.lowercase().contains(busqueda) ||
                it.description.lowercase().contains(busqueda) ||
                it.location.lowercase().contains(busqueda))
    }

    val hayResultados = matchingCampaigns.isNotEmpty() ||
            matchingActivities.isNotEmpty() ||
            matchingEventos.isNotEmpty() ||
            matchingTalleres.isNotEmpty()

    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(color = Surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Box(
                            modifier = Modifier.clip(CircleShape).background(TealLight.copy(alpha = 0.45f)).padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = TealPrimary)
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Explorar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = "Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(36.dp).clip(CircleShape).clickable { onPerfilClick() }
                    )
                }
            }
        },
        bottomBar = {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(TealLight.copy(alpha = 0.12f)).padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        placeholder = { Text("Buscar causas, asociaciones...") },
                        leadingIcon = { Icon(Icons.Default.Search, "Buscar", tint = TealPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface,
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(16.dp))
                    ExplorarFilters(filtroActual) { filtroActual = it }
                    Spacer(Modifier.height(28.dp))
                }

                if (cargandoActividades) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            color = TealPrimary
                        )
                        Text(
                            "Cargando actividades...",
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (!errorActividades.isNullOrBlank()) {
                    item {
                        Spacer(Modifier.height(12.dp))
                        Text(errorActividades, fontSize = 14.sp, color = BrownPrimary)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onReintentarActividades,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) { Text("Reintentar") }
                    }
                }

                if (matchingCampaigns.isNotEmpty()) {
                    item {
                        SectionTitle("Campañas")
                    }

                    items(matchingCampaigns) { campania ->
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
                        Spacer(Modifier.height(20.dp))
                    }
                }

                if (matchingActivities.isNotEmpty()) {
                    item {
                        SectionTitle("Actividades")
                    }

                    items(matchingActivities) { actividad ->
                        val cupos = actividad.capacidadPersonas
                        val ocupados = estadoActividades?.ocupados(actividad.id) ?: 0
                        val disponibles = estadoActividades?.disponibles(actividad.id) ?: cupos

                        ActivityCard(
                            title = actividad.nombre,
                            organization = actividad.organizacion,
                            participants = "$ocupados/$cupos familias · $disponibles lugares disponibles",
                            date = actividad.fecha,
                            onClick = { onActividadClick(actividad.id) }
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                }

                if (matchingEventos.isNotEmpty()) {
                    item {
                        SectionTitle("Eventos")
                    }

                    items(matchingEventos) { evento ->
                        EventCard(
                            title = evento.title,
                            description = evento.description,
                            location = evento.location,
                            date = evento.starts_at,
                            onClick = { onEventoClick(evento.id) }
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                }

                if (matchingTalleres.isNotEmpty()) {
                    item {
                        SectionTitle("Talleres")
                    }

                    items(matchingTalleres) { taller ->
                        WorkshopCard(
                            title = taller.title,
                            description = taller.description,
                            location = taller.location,
                            date = taller.starts_at,
                            onClick = { onTallerClick(taller.id) }
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                }

                if (!hayResultados && !cargandoActividades && errorActividades.isNullOrBlank()) {
                    item {
                        Text(
                            "No encontramos resultados",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ExplorarFilters(
    filtroActual: String,
    onFiltroChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val opciones = listOf("Todos", "Voluntariado", "Donaciones", "Campañas", "Eventos", "Talleres")

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            FilterChip(
                selected = filtroActual == opcion,
                onClick = { onFiltroChange(opcion) },
                label = {
                    Text(
                        opcion,
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.reforestacionurbana),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(22.dp))
            )

            Column(Modifier.padding(12.dp)) {
                Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                Spacer(Modifier.height(6.dp))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(5.dp))
                Text(organization, fontSize = 14.sp, color = TealDark)
                Spacer(Modifier.height(10.dp))
                Text(description, fontSize = 14.sp, color = TextSecondary)
                Spacer(Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)),
                    color = TealPrimary,
                    trackColor = TealLight.copy(alpha = 0.35f)
                )

                Spacer(Modifier.height(6.dp))
                Text(progressText, fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.utilesescolares),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(22.dp))
            )

            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, tint = TealPrimary, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
                }

                Spacer(Modifier.height(10.dp))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(5.dp))
                Text(organization, fontSize = 14.sp, color = TealDark)
                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(participants, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Ver actividad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    title: String,
    description: String,
    location: String,
    date: String,
    onClick: () -> Unit
) {
    SimpleItemCard(
        title = title,
        description = description,
        location = location,
        date = date,
        buttonText = "Ver evento",
        onClick = onClick
    )
}

@Composable
private fun WorkshopCard(
    title: String,
    description: String,
    location: String,
    date: String,
    onClick: () -> Unit
) {
    SimpleItemCard(
        title = title,
        description = description,
        location = location,
        date = date,
        buttonText = "Ver taller",
        onClick = onClick
    )
}

@Composable
private fun SimpleItemCard(
    title: String,
    description: String,
    location: String,
    date: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(date.replace("T", " ").take(16), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
            }

            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(description, fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(location, fontSize = 14.sp, color = TextSecondary)
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}