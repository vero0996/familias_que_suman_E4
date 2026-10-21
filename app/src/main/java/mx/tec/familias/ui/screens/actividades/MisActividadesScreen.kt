package mx.tec.familias.ui.screens.actividades

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class ActividadInscrita(
    val id: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String
)

@Composable
fun MisActividadesScreen(
    actividadesViewModel: mx.tec.familias.viewmodel.ActividadesViewModel,
    familiaId: String,
    onInicioClick: () -> Unit = {},
    onExplorarClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCalendarioClick: () -> Unit = {},
    mostrarMensajes: Boolean = true
) {

    val estado = actividadesViewModel.estado

    LaunchedEffect(familiaId) {
        actividadesViewModel.cargarMisInscripcionesYEncuestas(familiaId)
    }

    val propias = estado.inscripciones

    val confirmadas = propias.filter {
        it.estado == mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
    }

    val enEspera = propias.filter {
        it.estado == mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA
    }

    var actividadEncuesta by remember {
        mutableStateOf<ActividadInscrita?>(null)
    }

    var calificacionCampania by remember { mutableStateOf(5) }
    var calificacionOrganizacion by remember { mutableStateOf(5) }
    var calificacionComunicacion by remember { mutableStateOf(5) }
    var calificacionGeneral by remember { mutableStateOf(5) }

    var comentarioEncuesta by remember { mutableStateOf("") }
    var mensajeEncuesta by remember { mutableStateOf<String?>(null) }

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
                top = 16.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.90f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column {
                        TopBar(
                            showProfile = true,
                            onProfileClick = onPerfilClick
                        )

                        Row(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 14.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = onCalendarioClick,
                                label = {
                                    Text(
                                        text = "Calendario",
                                        fontSize = 14.sp
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
                                    containerColor = Surface.copy(alpha = 0.84f),
                                    labelColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )

                            FilterChip(
                                selected = true,
                                onClick = {},
                                label = {
                                    Text(
                                        text = "Mis Actividades",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Surface,
                                    containerColor = Surface.copy(alpha = 0.84f),
                                    labelColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                        }
                    }
                }
            }

            actividadesViewModel.error?.let { mensaje ->
                item {
                    Text(
                        text = mensaje,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                SectionHeader(
                    "Mis actividades",
                    "Inscripciones confirmadas de tu familia."
                )
            }

            if (confirmadas.isEmpty()) {
                item {
                    EmptyActivitiesCard(
                        Icons.Default.EventAvailable,
                        "No tienes actividades inscritas",
                        "Explora las actividades disponibles para participar."
                    )
                }
            }

            items(
                confirmadas,
                key = { "confirmada:${it.id}" }
            ) { solicitud ->
                val evento = estado.evento(solicitud.eventoId)
                val registroId = solicitud.id.toIntOrNull()

                val encuestaCompletada =
                    registroId != null &&
                            actividadesViewModel.encuestasEnviadas.any {
                                it.registration_id == registroId
                            }

                val actividadTerminada =
                    evento.endsAtEpochMillis?.let {
                        it > 0L && it <= System.currentTimeMillis()
                    } ?: false

                val actividad = ActividadInscrita(
                    id = solicitud.id,
                    nombre = evento.nombre,
                    fecha = evento.fecha,
                    hora = evento.hora,
                    lugar = evento.lugar
                )

                ActividadInscritaCard(
                    actividad = actividad,
                    mostrarBotonEncuesta =
                        actividadTerminada && !encuestaCompletada,
                    encuestaCompletada = encuestaCompletada,
                    onContestarEncuesta = {
                        actividadEncuesta = actividad
                        calificacionCampania = 5
                        calificacionOrganizacion = 5
                        calificacionComunicacion = 5
                        calificacionGeneral = 5
                        comentarioEncuesta = ""
                        mensajeEncuesta = null
                    }
                )
            }

            item {
                SectionHeader(
                    "Lista de espera",
                    "Solicitudes pendientes de un lugar para tu familia."
                )
            }

            if (enEspera.isEmpty()) {
                item {
                    EmptyActivitiesCard(
                        Icons.Default.Schedule,
                        "No estás en ninguna lista de espera",
                        "Aquí aparecerán tus solicitudes cuando no haya cupo suficiente."
                    )
                }
            }

            items(
                enEspera,
                key = { "espera:${it.id}" }
            ) { solicitud ->
                val evento = estado.evento(solicitud.eventoId)

                ActividadInscritaCard(
                    actividad = ActividadInscrita(
                        id = solicitud.id,
                        nombre = evento.nombre,
                        fecha = evento.fecha,
                        hora = evento.hora,
                        lugar = evento.lugar
                    ),
                    enEspera = true
                )
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.ACTIVIDADES,
            mostrarMensajes = mostrarMensajes,
            onDestinationSelected = { destination ->
                when (destination) {
                    FamilyDestination.INICIO -> onInicioClick()
                    FamilyDestination.EXPLORAR -> onExplorarClick()
                    FamilyDestination.ACTIVIDADES -> Unit
                    FamilyDestination.MENSAJES -> onMensajesClick()
                    FamilyDestination.PERFIL -> onPerfilClick()
                }
            }
        )
    }


    actividadEncuesta?.let { actividad ->
        AlertDialog(
            onDismissRequest = {
                if (!actividadesViewModel.cargandoEncuestas) {
                    actividadEncuesta = null
                }
            },
            title = {
                Text(
                    text = "Cuéntanos tu experiencia",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = actividad.nombre,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Califica cada aspecto del 1 al 5.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    CalificacionEncuesta(
                        titulo = "La campaña",
                        valor = calificacionCampania,
                        onSeleccionar = {
                            calificacionCampania = it
                        }
                    )

                    CalificacionEncuesta(
                        titulo = "La organización",
                        valor = calificacionOrganizacion,
                        onSeleccionar = {
                            calificacionOrganizacion = it
                        }
                    )

                    CalificacionEncuesta(
                        titulo = "La comunicación",
                        valor = calificacionComunicacion,
                        onSeleccionar = {
                            calificacionComunicacion = it
                        }
                    )

                    CalificacionEncuesta(
                        titulo = "Experiencia general",
                        valor = calificacionGeneral,
                        onSeleccionar = {
                            calificacionGeneral = it
                        }
                    )

                    OutlinedTextField(
                        value = comentarioEncuesta,
                        onValueChange = {
                            comentarioEncuesta = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Comentario (opcional)")
                        },
                        placeholder = {
                            Text("¿Qué te gustó o qué mejorarías?")
                        },
                        minLines = 3,
                        maxLines = 5,
                        enabled = !actividadesViewModel.cargandoEncuestas
                    )

                    mensajeEncuesta?.let { mensaje ->
                        Text(
                            text = mensaje,
                            color =
                                if (mensaje.startsWith("¡Gracias")) {
                                    TealPrimary
                                } else {
                                    androidx.compose.material3.MaterialTheme
                                        .colorScheme
                                        .error
                                },
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled =
                        !actividadesViewModel.cargandoEncuestas &&
                                mensajeEncuesta
                                    ?.startsWith("¡Gracias") != true,
                    onClick = {
                        actividadesViewModel.enviarEncuesta(
                            registrationId = actividad.id,
                            campaignRating = calificacionCampania,
                            organizationRating = calificacionOrganizacion,
                            communicationRating = calificacionComunicacion,
                            overallRating = calificacionGeneral,
                            comment = comentarioEncuesta
                        ) { exito, mensaje ->

                            mensajeEncuesta = mensaje

                            if (exito) {
                                actividadEncuesta = null
                            }
                        }
                    }
                ) {
                    Text(
                        if (actividadesViewModel.cargandoEncuestas) {
                            "Enviando..."
                        } else {
                            "Enviar encuesta"
                        },
                        color = TealPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !actividadesViewModel.cargandoEncuestas,
                    onClick = {
                        actividadEncuesta = null
                    }
                ) {
                    Text(
                        "Cancelar",
                        color = TextSecondary
                    )
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
private fun CalificacionEncuesta(
    titulo: String,
    valor: Int,
    onSeleccionar: (Int) -> Unit
) {
    Column {
        Text(
            text = titulo,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { calificacion ->
                TextButton(
                    onClick = {
                        onSeleccionar(calificacion)
                    }
                ) {
                    Text(
                        text =
                            if (calificacion <= valor) {
                                "★"
                            } else {
                                "☆"
                            },
                        color =
                            if (calificacion <= valor) {
                                OrangePrimary
                            } else {
                                TextSecondary
                            },
                        fontSize = 25.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun ActividadInscritaCard(
    actividad: ActividadInscrita,
    mostrarBotonEncuesta: Boolean = false,
    encuestaCompletada: Boolean = false,
    enEspera: Boolean = false,
    onContestarEncuesta: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(28.dp),
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
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            DateBox(
                fecha = actividad.fecha,
                backgroundColor = OrangePrimary
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.nombre,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusBadge(
                    text = if (enEspera) "EN ESPERA" else "INSCRITO",
                    backgroundColor = if (enEspera) {
                        TealLight
                    } else {
                        OrangePrimary.copy(alpha = 0.45f)
                    },
                    textColor = if (enEspera) TealPrimary else BrownPrimary
                )

                if (enEspera) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tu inscripción se confirmará automáticamente " +
                                "si se liberan lugares suficientes para tu familia " +
                                "y corresponde tu turno.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.Schedule,
                    text = actividad.hora
                )

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    encuestaCompletada -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Encuesta completada",
                                color = TealPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    mostrarBotonEncuesta -> {
                        OutlinedButton(
                            onClick = onContestarEncuesta,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Contestar encuesta")
                        }
                    }
                }

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
private fun DateBox(
    fecha: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                backgroundColor.copy(alpha = 0.65f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = fecha.substringBefore(" "),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )

            Text(
                text = fecha.substringAfter(" "),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text = text,
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
                .background(
                    TealLight.copy(alpha = 0.50f)
                ),
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
private fun EmptyActivitiesCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        TealLight.copy(alpha = 0.65f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}