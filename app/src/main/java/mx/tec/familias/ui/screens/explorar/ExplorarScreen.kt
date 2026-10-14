package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
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
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.components.TopBar
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
    mostrarMensajes: Boolean = false
) {
    var searchText by remember { mutableStateOf("") }
    var filtroActual by remember { mutableStateOf("Todos") }
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloCampaniaSeleccionada by remember { mutableStateOf("") }

    val busqueda = searchText.trim().lowercase()

    val mostrarFamiliaMisionera = filtroActual == "Todos" || filtroActual == "Voluntariado"
    val mostrarPosada = filtroActual == "Todos" || filtroActual == "Eventos"
    val mostrarCampanias = filtroActual == "Todos" || filtroActual == "Campañas"
    val mostrarDonaciones = filtroActual == "Todos" || filtroActual == "Donaciones"

    val coincideCampania1 = listOf("útiles escolares para todos", "fundación aprender juntos").any { it.contains(busqueda) }
    val coincideFamiliaMisionera = listOf("familia misionera noviembre", "familias que suman").any { it.contains(busqueda) }
    val coincidePosada = listOf("posada sendero", "familias que suman").any { it.contains(busqueda) }
    val coincideSillas = listOf("sillas de ruedas para todos").any { it.contains(busqueda) }
    val coincideJuguetes = listOf("juguetón familiar navideño").any { it.contains(busqueda) }

    val hayCampanias = mostrarCampanias && coincideCampania1
    val hayFamiliaMisionera = mostrarFamiliaMisionera && coincideFamiliaMisionera
    val hayPosada = mostrarPosada && coincidePosada
    val hayDonaciones = mostrarDonaciones && (coincideSillas || coincideJuguetes)

    val hayActividades = hayFamiliaMisionera || hayPosada
    val hayResultados = hayCampanias || hayActividades || hayDonaciones

    Scaffold(
        containerColor = Background,
        topBar = {
            TopBar(
                onProfileClick = onPerfilClick
            )
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = { Text(text = "Buscar causas, asociaciones...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = TealPrimary) },
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
                ExplorarFilters(filtroActual = filtroActual, onFiltroChange = { filtroActual = it })
                Spacer(modifier = Modifier.height(28.dp))
            }

            // SECCIÓN DONACIONES (Agregada según tu requerimiento)
            if (hayDonaciones) {
                item {
                    Text(text = "Donaciones Activas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (coincideSillas) {
                    item {
                        DonationItemCard(
                            titulo = "Sillas de Ruedas para Todos",
                            descripcion = "Apoya a adultos mayores en situación vulnerable.",
                            meta = "Meta: 15 unidades",
                            imagenRes = R.drawable.comedorcomunitario,
                            onDonarClick = {
                                tituloCampaniaSeleccionada = "Sillas de Ruedas"
                                mostrarDialogoDonacion = true
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                if (coincideJuguetes) {
                    item {
                        DonationItemCard(
                            titulo = "Juguetón Familiar Navideño",
                            descripcion = "Dona juguetes en excelente estado para casas hogar.",
                            meta = "Meta: 300 juguetes",
                            imagenRes = R.drawable.utilesescolares,
                            onDonarClick = {
                                tituloCampaniaSeleccionada = "Juguetes Navideños"
                                mostrarDialogoDonacion = true
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // SECCIÓN CAMPAÑAS
            if (hayCampanias) {
                item {
                    Text(text = "Campañas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    CampaignCard(
                        tag = "URGENTE", tagColor = BrownPrimary,
                        title = "Útiles Escolares para Todos", organization = "Fundación Aprender Juntos",
                        description = "Ayuda a que niñas y niños comiencen el ciclo escolar con todo lo necesario.",
                        progress = 0.80f, progressText = "80%", buttonText = "Ver campaña",
                        onClick = { onCampaniaClick("utiles_escolares") }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // SECCIÓN ACTIVIDADES
            if (hayActividades) {
                item {
                    Text(text = "Actividades", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (hayFamiliaMisionera) {
                    item {
                        val eventoId = "familia_misionera_noviembre"
                        val evento = estadoActividades?.eventoOrNull(eventoId)
                        val cupos = evento?.capacidadPersonas ?: 25
                        val ocupados = estadoActividades?.ocupados(eventoId) ?: 0
                        val disponibles = estadoActividades?.disponibles(eventoId) ?: 25
                        ActivityCard(
                            title = evento?.nombre ?: "Familia Misionera Noviembre",
                            organization = evento?.organizacion ?: "Familias que Suman",
                            participants = "$ocupados/$cupos familias · $disponibles lugares disponibles",
                            date = evento?.fecha ?: "28 Nov",
                            onClick = { onActividadClick(eventoId) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                if (hayPosada) {
                    item {
                        val eventoId = "posada_sendero"
                        val evento = estadoActividades?.eventoOrNull(eventoId)
                        val cupos = evento?.capacidadPersonas ?: 15
                        val ocupados = estadoActividades?.ocupados(eventoId) ?: 0
                        val disponibles = estadoActividades?.disponibles(eventoId) ?: 15
                        ActivityCard(
                            title = evento?.nombre ?: "Posada Sendero",
                            organization = evento?.organizacion ?: "Familias que Suman",
                            participants = "$ocupados/$cupos familias · $disponibles lugares disponibles",
                            date = evento?.fecha ?: "28 Nov",
                            onClick = { onActividadClick(eventoId) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (!hayResultados) {
                item {
                    Text(text = "No encontramos resultados", modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }

    // Diálogo de donación
    if (mostrarDialogoDonacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDonacion = false },
            title = { Text("Donar a: $tituloCampaniaSeleccionada", color = TealPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { mostrarDialogoDonacion = false }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Entendido", color = TextOnPrimary)
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
private fun ExplorarFilters(filtroActual: String, onFiltroChange: (String) -> Unit) {
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
                label = { Text(text = opcion, fontWeight = if (filtroActual == opcion) FontWeight.Bold else FontWeight.Normal) },
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

// DonationItemCard traída desde InicioScreen
@Composable
private fun DonationItemCard(titulo: String, descripcion: String, meta: String, imagenRes: Int, onDonarClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Image(painter = painterResource(id = imagenRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(130.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = descripcion, fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = meta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrownPrimary)
                    Button(onClick = onDonarClick, colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), modifier = Modifier.height(36.dp)) {
                        Text("Donar", fontSize = 13.sp, color = TextOnPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CampaignCard(tag: String, tagColor: Color, title: String, organization: String, description: String, progress: Float, progressText: String, buttonText: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(painter = painterResource(id = R.drawable.reforestacionurbana), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(22.dp)))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, color = TealDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = description, fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(18.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = TealPrimary, trackColor = TealLight.copy(alpha = 0.35f))
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(title: String, organization: String, participants: String, date: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(painter = painterResource(id = R.drawable.utilesescolares), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(22.dp)))
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, color = TealDark)
                Spacer(modifier = Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = participants, fontSize = 14.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text(text = "Ver actividad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}