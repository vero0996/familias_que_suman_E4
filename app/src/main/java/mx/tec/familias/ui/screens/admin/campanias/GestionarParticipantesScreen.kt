package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.EstadoParticipante
import mx.tec.familias.data.model.ParticipanteActividadAdmin
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.Error
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.ui.theme.WarningBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarParticipantesScreen(
    participantesIniciales: List<ParticipanteActividadAdmin>,
    cuposTotales: Int,
    onBackClick: () -> Unit = {},
    onParticipantesChanged: (List<ParticipanteActividadAdmin>) -> Unit = {}
) {

    var participantes by remember {
        mutableStateOf(participantesIniciales)
    }

    var participanteSeleccionado by remember {
        mutableStateOf<ParticipanteActividadAdmin?>(null)
    }

    var mostrarDialogoEliminar by remember {
        mutableStateOf(false)
    }

    var mostrarDialogoAsignar by remember {
        mutableStateOf(false)
    }

    val confirmados = participantes.filter {
        it.estado == EstadoParticipante.CONFIRMADO
    }

    val listaEspera = participantes.filter {
        it.estado == EstadoParticipante.LISTA_ESPERA
    }

    val personasConfirmadas = confirmados.sumOf {
        it.integrantes.size
    }

    val lugaresDisponibles =
        (cuposTotales - personasConfirmadas).coerceAtLeast(0)

    val progreso =
        if (cuposTotales > 0) {
            (personasConfirmadas.toFloat() / cuposTotales.toFloat())
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    Scaffold(
        containerColor = Background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gestionar participantes",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // RESUMEN DE CUPOS
            // ==========================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Divider
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(
                                modifier = Modifier.size(8.dp)
                            )

                            Text(
                                text = "Cupos de la actividad",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Text(
                            text = "$personasConfirmadas / $cuposTotales",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = TealLight,
                        trackColor = Divider
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = if (lugaresDisponibles > 0) {
                            "$lugaresDisponibles lugares disponibles"
                        } else {
                            "Todos los lugares están ocupados"
                        },
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==========================================
            // PARTICIPANTES CONFIRMADOS
            // ==========================================

            Text(
                text = "Participantes confirmados",
                color = TealPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "${confirmados.size} familias confirmadas",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (confirmados.isEmpty()) {

                EmptyParticipantsCard(
                    text = "Todavía no hay participantes confirmados."
                )

            } else {

                confirmados.forEach { participante ->

                    ParticipanteCard(
                        participante = participante,
                        onEliminarClick = {
                            participanteSeleccionado = participante
                            mostrarDialogoEliminar = true
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ==========================================
            // LISTA DE ESPERA
            // ==========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Lista de espera",
                    color = TealPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (listaEspera.isNotEmpty()) {

                    Text(
                        text = "${listaEspera.size}",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Familias que esperan un lugar disponible.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (listaEspera.isEmpty()) {

                EmptyParticipantsCard(
                    text = "No hay familias en lista de espera."
                )

            } else {

                listaEspera.forEach { participante ->

                    ParticipanteEsperaCard(
                        participante = participante,
                        puedeAsignar = lugaresDisponibles > 0,

                        onAsignarClick = {

                            participanteSeleccionado = participante
                            mostrarDialogoAsignar = true
                        },

                        onEliminarClick = {

                            participanteSeleccionado = participante
                            mostrarDialogoEliminar = true
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }

    // ==========================================
    // DIÁLOGO ELIMINAR
    // ==========================================

    if (mostrarDialogoEliminar && participanteSeleccionado != null) {

        val participante = participanteSeleccionado!!

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEliminar = false
                participanteSeleccionado = null
            },

            title = {
                Text(
                    text = "¿Sacar participante?"
                )
            },

            text = {
                Text(
                    text = "¿Quieres sacar a ${participante.nombreFamilia} de esta actividad?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val nuevosParticipantes = participantes.filter {
                            it.id != participante.id
                        }

                        participantes = nuevosParticipantes
                        onParticipantesChanged(nuevosParticipantes)

                        mostrarDialogoEliminar = false
                        participanteSeleccionado = null
                    }
                ) {

                    Text(
                        text = "Sacar",
                        color = Error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        participanteSeleccionado = null
                    }
                ) {

                    Text(
                        text = "Cancelar",
                        color = TealPrimary
                    )
                }
            }
        )
    }

    // ==========================================
    // DIÁLOGO ASIGNAR LUGAR
    // ==========================================

    if (mostrarDialogoAsignar && participanteSeleccionado != null) {

        val participante = participanteSeleccionado!!

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoAsignar = false
                participanteSeleccionado = null
            },

            title = {
                Text(
                    text = "Asignar lugar"
                )
            },

            text = {
                Text(
                    text = "¿Quieres asignar un lugar a ${participante.nombreFamilia}?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (lugaresDisponibles >= participante.integrantes.size) {

                            val nuevosParticipantes = participantes.map {

                                if (it.id == participante.id) {

                                    it.copy(
                                        estado = EstadoParticipante.CONFIRMADO,
                                        lugaresAsignados = (
                                                it.lugaresAsignados +
                                                        (personasConfirmadas + 1..personasConfirmadas + it.integrantes.size)
                                                ).toList()
                                    )

                                } else {
                                    it
                                }
                            }

                            participantes = nuevosParticipantes
                            onParticipantesChanged(nuevosParticipantes)
                        }

                        mostrarDialogoAsignar = false
                        participanteSeleccionado = null
                    }
                ) {

                    Text(
                        text = "Asignar",
                        color = TealPrimary
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        mostrarDialogoAsignar = false
                        participanteSeleccionado = null
                    }
                ) {

                    Text(
                        text = "Cancelar",
                        color = TealPrimary
                    )
                }
            }
        )
    }
}


// ======================================================
// TARJETA PARTICIPANTE CONFIRMADO
// ======================================================

@Composable
private fun ParticipanteCard(
    participante: ParticipanteActividadAdmin,
    onEliminarClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Divider
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = participante.nombreFamilia,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    if (participante.lugaresAsignados.isNotEmpty()) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(5.dp)
                            )

                            Text(
                                text = "Lugares: ${
                                    participante.lugaresAsignados.joinToString(", ")
                                }",
                                color = TealPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Confirmado",
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ParticipantInfoRow(
                icon = Icons.Default.Email,
                text = participante.correo
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            ParticipantInfoRow(
                icon = Icons.Default.Phone,
                text = participante.telefono
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "${participante.integrantes.size} integrantes",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = onEliminarClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = null,
                    tint = Error
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Sacar participante",
                    color = Error
                )
            }
        }
    }
}


// ======================================================
// TARJETA LISTA DE ESPERA
// ======================================================

@Composable
private fun ParticipanteEsperaCard(
    participante: ParticipanteActividadAdmin,
    puedeAsignar: Boolean,
    onAsignarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Divider
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = participante.nombreFamilia,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text(
                            text = "En espera",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "ESPERA",
                    color = Error,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            WarningBackground,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(
                            horizontal = 7.dp,
                            vertical = 5.dp
                        )
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ParticipantInfoRow(
                icon = Icons.Default.Email,
                text = participante.correo
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            ParticipantInfoRow(
                icon = Icons.Default.Phone,
                text = participante.telefono
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onAsignarClick,
                enabled = puedeAsignar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = if (puedeAsignar) {
                        "Asignar lugar"
                    } else {
                        "Sin lugares disponibles"
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onEliminarClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Sacar de lista de espera",
                    color = Error
                )
            }
        }
    }
}


// ======================================================
// INFORMACIÓN DEL PARTICIPANTE
// ======================================================

@Composable
private fun ParticipantInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}


// ======================================================
// ESTADO VACÍO
// ======================================================

@Composable
private fun EmptyParticipantsCard(
    text: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Divider
        )
    ) {

        Text(
            text = text,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}