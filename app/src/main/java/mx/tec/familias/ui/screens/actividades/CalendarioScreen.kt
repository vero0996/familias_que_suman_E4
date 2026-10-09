package mx.tec.familias.ui.screens.actividades

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.components.TopBar
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class ActividadCalendario(
    val id: String,
    val nombre: String,
    val fecha: Int,
    val mes: String,
    val diaSemana: String,
    val hora: String,
    val lugar: String,
    val estado: String
)

@Composable
fun CalendarioScreen(
    onInicioClick: () -> Unit = {},
    onExplorarClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onActividadClick: (ActividadCalendario) -> Unit = {},
    onMisActividadesClick: () -> Unit = {},
    estadoActividades: mx.tec.familias.data.model.EstadoActividades,
    familiaId: String,
    mostrarMensajes: Boolean = true
) {
    var diaSeleccionado by remember { mutableIntStateOf(24) }

    val actividades = estadoActividades.eventos.map { evento ->
        val solicitud = estadoActividades.inscripciones.firstOrNull {
            it.eventoId == evento.id && it.familiaId == familiaId
        }

        ActividadCalendario(
            id = evento.id,
            nombre = evento.nombre,
            fecha = evento.fecha.substringBefore(" ").toInt(),
            mes = "NOV",
            diaSemana = if (evento.id == "arboles") "Martes" else "Miércoles",
            hora = evento.hora,
            lugar = evento.lugar,
            estado = when (solicitud?.estado) {
                mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA -> "INSCRITO"
                mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA -> "EN ESPERA"
                null -> if (estadoActividades.disponibles(evento.id) == 0) "CUPO LLENO" else "DISPONIBLE"
            }
        )
    }

    val actividadesDelDia = actividades.filter {
        it.fecha == diaSeleccionado
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            title = "Calendario",
                            showProfile = true,
                            onProfileClick = onPerfilClick
                        )

                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = true,
                                onClick = {},
                                label = {
                                    Text(
                                        text = "Calendario",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Surface,
                                    selectedLeadingIconColor = Surface,
                                    containerColor = Surface.copy(alpha = 0.84f),
                                    labelColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )

                            FilterChip(
                                selected = false,
                                onClick = onMisActividadesClick,
                                label = {
                                    Text(
                                        text = "Mis Actividades",
                                        fontSize = 14.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Surface.copy(alpha = 0.84f),
                                    labelColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                        }
                    }
                }
            }

            item {
                CalendarioMensual(
                    diaSeleccionado = diaSeleccionado,
                    onDiaSeleccionado = { diaSeleccionado = it },
                    actividades = actividades
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LegendItem(
                        color = TealDark,
                        text = "Disponible"
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    LegendItem(
                        color = OrangePrimary,
                        text = "Inscrito"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Domingo, $diaSeleccionado de Nov.",
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${actividadesDelDia.size} Actividades",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (actividadesDelDia.isEmpty()) {
                item {
                    EmptyDayCard()
                }
            } else {
                items(
                    items = actividadesDelDia,
                    key = { it.id }
                ) { actividad ->
                    ActividadCalendarioCard(
                        actividad = actividad,
                        onClick = {
                            onActividadClick(actividad)
                        }
                    )
                }
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.ACTIVIDADES,
            mostrarMensajes = mostrarMensajes,
            onDestinationSelected = { destination ->
                when (destination) {
                    FamilyDestination.INICIO -> onInicioClick()
                    FamilyDestination.EXPLORAR -> onExplorarClick()
                    FamilyDestination.ACTIVIDADES -> {}
                    FamilyDestination.MENSAJES -> onMensajesClick()
                    FamilyDestination.PERFIL -> onPerfilClick()
                }
            }
        )
    }
}

@Composable
private fun CalendarioMensual(
    diaSeleccionado: Int,
    onDiaSeleccionado: (Int) -> Unit,
    actividades: List<ActividadCalendario>
) {
    val diasSemana = listOf(
        "LUN",
        "MAR",
        "MIÉ",
        "JUE",
        "VIE",
        "SÁB",
        "DOM"
    )

    val diasConActividades = actividades.groupBy { it.fecha }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Noviembre 2026",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TealLight.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendario",
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                diasSemana.forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            /*
             * Noviembre 2026 comienza en domingo.
             * Por eso dejamos 6 espacios antes del día 1.
             */
            val totalDias = 30
            val primerDia = 6

            val celdas = (0 until primerDia).map {
                null
            } + (1..totalDias).map {
                it
            }

            celdas.chunked(7).forEach { semana ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    semana.forEach { dia ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dia != null) {
                                val seleccionado = dia == diaSeleccionado
                                val actividadesDelDia = diasConActividades[dia]

                                Column(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (seleccionado) {
                                                TealLight.copy(alpha = 0.65f)
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            onDiaSeleccionado(dia)
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dia.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (seleccionado) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = TextPrimary
                                    )

                                    if (!actividadesDelDia.isNullOrEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            actividadesDelDia
                                                .take(2)
                                                .forEach { actividad ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (actividad.estado == "INSCRITO") {
                                                                    OrangePrimary
                                                                } else {
                                                                    TealDark
                                                                }
                                                            )
                                                    )
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (semana.size < 7) {
                        repeat(7 - semana.size) {
                            Spacer(
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    text: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.80f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = text,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ActividadCalendarioCard(
    actividad: ActividadCalendario,
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
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (actividad.estado == "INSCRITO") {
                            OrangePrimary.copy(alpha = 0.65f)
                        } else {
                            TealLight.copy(alpha = 0.65f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = actividad.fecha.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )

                    Text(
                        text = actividad.mes,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = actividad.nombre,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusBadge(
                    estado = actividad.estado
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.Schedule,
                    text = actividad.hora
                )

                Spacer(modifier = Modifier.height(6.dp))

                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = actividad.lugar
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    estado: String
) {
    val backgroundColor =
        if (estado == "INSCRITO") {
            OrangePrimary.copy(alpha = 0.45f)
        } else {
            TealLight.copy(alpha = 0.65f)
        }

    val textColor =
        if (estado == "INSCRITO") {
            BrownPrimary
        } else {
            TealPrimary
        }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TealLight.copy(alpha = 0.50f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = TealPrimary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptyDayCard() {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TealLight.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No hay actividades este día",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Selecciona otro día para consultar las actividades disponibles.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}