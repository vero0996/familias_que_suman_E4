package mx.tec.familias.ui.screens.admin.dashboard

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.*
import mx.tec.familias.viewmodel.EditablePublication

@Composable
fun DashboardAdminScreen(
    tituloActividad: String,
    participantesActividad: Int,
    cuposTotalesActividad: Int,
    fechaActividad: String = "",
    horaActividad: String = "",
    actividadId: Int? = null,
    actividadStatus: String = "",
    managedActivities: List<EditablePublication> = emptyList(),
    isLoadingActivities: Boolean = false,
    activityError: String? = null,
    actividadSecundaria: EditablePublication? = null,
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCrearCampaniaClick: () -> Unit = {},
    onActividadClick: (Int) -> Unit = {}
) {
    var mostrarAccionesUrgentes by remember { mutableStateOf(false) }
    var mostrarCancelarCampania by remember { mutableStateOf(false) }
    var mostrarDialogoCompartir by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val codigoCampania = remember {
        ('A'..'Z').shuffled().take(3).joinToString("") + (100..999).random().toString()
    }

    val urlCampania = "https://familiasquesuman.app/campana/$codigoCampania"

    val cuposDisponibles = (cuposTotalesActividad - participantesActividad).coerceAtLeast(0)
    val campaniasActivas = 3

    val porcentajeCupos = if (cuposTotalesActividad > 0) {
        (participantesActividad.toFloat() / cuposTotalesActividad.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val porcentajeCuposTexto = "${(porcentajeCupos * 100).toInt()}%"

    val fechaHoraActividad = listOf(fechaActividad, horaActividad)
        .filter { it.isNotBlank() }
        .joinToString(" • ")
        .ifBlank { "Fecha por confirmar" }

    val cuposDisponiblesSecundaria = actividadSecundaria?.let {
        ((it.capacityPeople.toIntOrNull() ?: 0) - it.confirmedPeople).coerceAtLeast(0)
    } ?: 0

    val porcentajeCuposSecundaria = actividadSecundaria?.let {
        val capacity = it.capacityPeople.toIntOrNull() ?: 0

        if (capacity > 0) {
            (it.confirmedPeople.toFloat() / capacity).coerceIn(0f, 1f)
        } else {
            0f
        }
    } ?: 0f

    val displayActivities = remember(managedActivities, actividadSecundaria) {
        when {
            managedActivities.isNotEmpty() -> managedActivities
            actividadSecundaria != null -> listOf(actividadSecundaria)
            else -> emptyList()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TealLight.copy(alpha = 0.12f),

        topBar = {
            AdminScreenHeader(
                title = "Inicio",

                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Image(
                            painter = painterResource(id = R.drawable.fotoperfil),
                            contentDescription = "Perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            )
        },

        bottomBar = {
            AdminBottomNavigationBar(
                currentDestination = AdminDestination.INICIO,

                onDestinationSelected = { destination ->
                    when (destination) {
                        AdminDestination.INICIO -> Unit
                        AdminDestination.CAMPANIAS -> onCampaniasClick()
                        AdminDestination.MENSAJES -> onMensajesClick()
                        AdminDestination.CONFIGURACION -> onConfiguracionClick()
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = 18.dp,
                        start = 18.dp,
                        end = 18.dp,
                        bottom = 32.dp
                    )
            ) {

                // SALUDO

                Column(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Hola, Asociación Banco de Alimentos",
                        color = TealPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Aquí tienes el resumen de tu impacto hoy.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // NECESIDADES URGENTES

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .background(
                            WarningBackground,
                            RoundedCornerShape(22.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta",
                            tint = Error,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Necesidades Urgentes",
                            color = Error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (tituloActividad.isNotBlank()) {
                                "Revisa la disponibilidad de lugares para \"$tituloActividad\"."
                            } else {
                                "Revisa las próximas actividades de tu organización."
                            },
                            color = Error,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        IconButton(
                            onClick = { mostrarAccionesUrgentes = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más acciones",
                                tint = Error
                            )
                        }

                        DropdownMenu(
                            expanded = mostrarAccionesUrgentes,
                            onDismissRequest = { mostrarAccionesUrgentes = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Compartir campaña") },

                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = null
                                    )
                                },

                                onClick = {
                                    mostrarAccionesUrgentes = false
                                    mostrarDialogoCompartir = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Enviar mensaje") },

                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null
                                    )
                                },

                                onClick = {
                                    mostrarAccionesUrgentes = false
                                    onMensajesClick()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Cancelar campaña",
                                        color = Error
                                    )
                                },

                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Error
                                    )
                                },

                                onClick = {
                                    mostrarAccionesUrgentes = false
                                    mostrarCancelarCampania = true
                                }
                            )
                        }
                    }
                }

                // RESUMEN DE IMPACTO

                Text(
                    text = "Resumen de Impacto",
                    color = TealPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // KPIs PRINCIPALES

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Participantes",
                                tint = BrownPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = participantesActividad.toString(),
                                color = BrownPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "PARTICIPANTES",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = "Cupos disponibles",
                                tint = TealPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = cuposDisponibles.toString(),
                                color = TealPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "CUPOS DISPONIBLES",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // SEGUNDA FILA KPIs

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Campañas activas",
                                tint = TealPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = campaniasActivas.toString(),
                                color = TealPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "CAMPAÑAS ACTIVAS",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = "Cupos utilizados",
                                tint = BrownPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = porcentajeCuposTexto,
                                color = BrownPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "CUPOS UTILIZADOS",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // PARTICIPANTES VS CUPOS

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Participación en actividades",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$participantesActividad personas confirmadas de $cuposTotalesActividad lugares",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { porcentajeCupos },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = TealPrimary,
                            trackColor = TealLight.copy(alpha = 0.35f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "$participantesActividad participantes",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Divider)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "$cuposDisponibles disponibles",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // PARTICIPACIÓN POR ACTIVIDAD

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Participación por actividad",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ocupación de las próximas actividades",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (displayActivities.isEmpty()) {
                            Text(
                                text = "No hay actividades disponibles.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        } else {
                            displayActivities.take(2).forEachIndexed { index, actividad ->

                                val capacity = actividad.capacityPeople.toIntOrNull() ?: 0

                                val progresoActividad = if (capacity > 0) {
                                    (actividad.confirmedPeople.toFloat() / capacity.toFloat())
                                        .coerceIn(0f, 1f)
                                } else {
                                    0f
                                }

                                Text(
                                    text = actividad.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { progresoActividad },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = if (index == 0) TealPrimary else BrownPrimary,
                                    trackColor = TealLight.copy(alpha = 0.35f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${actividad.confirmedPeople} participantes",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = "$capacity cupos",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                if (index < displayActivities.take(2).lastIndex) {
                                    Spacer(modifier = Modifier.height(18.dp))
                                }
                            }
                        }
                    }
                }

                // PRÓXIMAS ACTIVIDADES

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximas Actividades",
                        color = TealPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Ver todas",
                        color = TealPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onCampaniasClick()
                        }
                    )
                }

                when {
                    isLoadingActivities -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = TealPrimary
                            )
                        }
                    }

                    !activityError.isNullOrBlank() -> {
                        Text(
                            text = activityError,
                            color = Error
                        )
                    }

                    displayActivities.isEmpty() -> {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color.White),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "No tienes actividades para mostrar.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }

                    else -> {
                        displayActivities.forEachIndexed { index, actividad ->

                            val capacity = actividad.capacityPeople.toIntOrNull() ?: 0

                            val available = (
                                    capacity -
                                            actividad.confirmedPeople
                                    ).coerceAtLeast(0)

                            val progress = if (capacity > 0) {
                                (
                                        actividad.confirmedPeople.toFloat() /
                                                capacity.toFloat()
                                        ).coerceIn(0f, 1f)
                            } else {
                                0f
                            }

                            Card(
                                shape = RoundedCornerShape(28.dp),
                                border = BorderStroke(1.dp, Color.White),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        onActividadClick(
                                            actividad.id
                                        )
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.height(IntrinsicSize.Min)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .fillMaxHeight()
                                            .background(
                                                if (index == 0) {
                                                    Error
                                                } else {
                                                    TealLight
                                                }
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = actividad.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = actividad.status
                                                    .replaceFirstChar {
                                                        it.uppercase()
                                                    },
                                                color = TealPrimary,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CalendarToday,
                                                contentDescription = "Fecha",
                                                modifier = Modifier.size(12.dp),
                                                tint = TextSecondary
                                            )

                                            Spacer(modifier = Modifier.width(4.dp))

                                            Text(
                                                text = actividad.startsAt
                                                    .replace("T", " ")
                                                    .take(16),
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }

                                        if (actividad.location.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.LocationOn,
                                                    contentDescription = "Ubicación",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = TextSecondary
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    text = actividad.location,
                                                    fontSize = 12.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Lugares disponibles",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )

                                            Text(
                                                text = "$available disponibles",
                                                fontSize = 12.sp,
                                                color = if (index == 0) Error else TealPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp),
                                            color = if (index == 0) Error else TealPrimary,
                                            trackColor = TealLight.copy(alpha = 0.35f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onCrearCampaniaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Crear campaña",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // DIÁLOGO COMPARTIR CAMPAÑA

    if (mostrarDialogoCompartir) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCompartir = false
            },

            containerColor = Surface,

            title = {
                Text(
                    text = "Compartir campaña",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },

            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Comparte esta campaña con otras familias:",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(
                                1.dp,
                                Divider,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp),

                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = urlCampania,
                            fontSize = 13.sp,
                            color = TealDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(
                                urlCampania
                            )
                        )

                        Toast.makeText(
                            context,
                            "Enlace copiado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = TealPrimary
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Copiar",
                        color = TealPrimary
                    )
                }
            },

            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            val shareIntent = Intent(
                                Intent.ACTION_SEND
                            ).apply {
                                type = "text/plain"

                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "¡Mira esta campaña en Familias que Suman!\n\n$tituloActividad\n\n$urlCampania"
                                )
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Compartir campaña"
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = TealPrimary
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "Compartir",
                            color = TealPrimary
                        )
                    }

                    TextButton(
                        onClick = {
                            mostrarDialogoCompartir = false
                        }
                    ) {
                        Text(
                            text = "Cerrar",
                            color = TextSecondary
                        )
                    }
                }
            }
        )
    }

    // DIÁLOGO CANCELAR CAMPAÑA

    if (mostrarCancelarCampania) {
        AlertDialog(
            onDismissRequest = {
                mostrarCancelarCampania = false
            },

            title = {
                Text(
                    text = "Cancelar campaña"
                )
            },

            text = {
                Text(
                    text = "¿Seguro que deseas cancelar esta campaña?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarCancelarCampania = false

                        Toast.makeText(
                            context,
                            "Campaña cancelada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        text = "Cancelar campaña",
                        color = Error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarCancelarCampania = false
                    }
                ) {
                    Text(
                        text = "Volver"
                    )
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.3f
)
@Composable
private fun DashboardAdminScreenPreview() {
    FamiliasQueSumanTheme(
        darkTheme = false
    ) {
        DashboardAdminScreen(
            tituloActividad = "Colecta de Invierno 2026",
            participantesActividad = 35,
            cuposTotalesActividad = 50
        )
    }
}