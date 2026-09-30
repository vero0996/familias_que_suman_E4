package mx.tec.familias.ui.screens.actividades

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.components.TopBar
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class ActividadInscrita(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String
)

data class ActividadListaEspera(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val posicion: Int
)

@Composable
fun MisActividadesScreen(
    onInicioClick: () -> Unit = {},
    onExplorarClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCalendarioClick: () -> Unit = {},
    mostrarMensajes: Boolean = true
) {

    var actividadesInscritas by remember {
        mutableStateOf(
            listOf(
                ActividadInscrita(
                    id = 1,
                    nombre = "Reforestación en el Parque Central",
                    fecha = "24 NOV",
                    hora = "09:00 AM – 12:00 PM",
                    lugar = "Parque Central"
                )
            )
        )
    }

    var actividadesEspera by remember {
        mutableStateOf(
            listOf(
                ActividadListaEspera(
                    id = 2,
                    nombre = "Taller de Reciclaje en Familia",
                    fecha = "24 NOV",
                    hora = "11:30 AM – 01:00 PM",
                    lugar = "Centro Comunitario",
                    posicion = 3
                )
            )
        )
    }

    var mostrarNotificacion by remember {
        mutableStateOf(true)
    }

    var actividadDisponibleId by remember {
        mutableStateOf<Int?>(2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                TopBar(
                    title = "Mis Actividades",
                    showProfile = true,
                    onProfileClick = onPerfilClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            containerColor = Surface,
                            labelColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(50)
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
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Surface,
                            selectedLeadingIconColor = Surface,
                            containerColor = Surface,
                            labelColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            if (mostrarNotificacion && actividadDisponibleId != null) {

                item {

                    LugarDisponibleCard(
                        nombreActividad = "Taller de Reciclaje en Familia",
                        onParticipar = {

                            val actividad = actividadesEspera.find {
                                it.id == actividadDisponibleId
                            }

                            if (actividad != null) {

                                actividadesEspera =
                                    actividadesEspera.filter {
                                        it.id != actividad.id
                                    }

                                actividadesInscritas =
                                    actividadesInscritas + ActividadInscrita(
                                        id = actividad.id,
                                        nombre = actividad.nombre,
                                        fecha = actividad.fecha,
                                        hora = actividad.hora,
                                        lugar = actividad.lugar
                                    )
                            }

                            actividadDisponibleId = null
                            mostrarNotificacion = false
                        },
                        onRechazar = {
                            mostrarNotificacion = false
                        }
                    )
                }
            }

            item {

                SectionHeader(
                    title = "Mis actividades",
                    subtitle = "Actividades en las que ya estás inscrita."
                )
            }

            if (actividadesInscritas.isEmpty()) {

                item {
                    EmptyActivitiesCard(
                        icon = Icons.Default.EventAvailable,
                        title = "No tienes actividades inscritas",
                        description = "Explora las actividades disponibles y encuentra una en la que quieras participar."
                    )
                }

            } else {

                items(
                    items = actividadesInscritas,
                    key = { it.id }
                ) { actividad ->

                    ActividadInscritaCard(
                        actividad = actividad
                    )
                }
            }

            item {

                SectionHeader(
                    title = "Lista de espera",
                    subtitle = "Aquí aparecerán las actividades que están llenas y en las que estás esperando un lugar."
                )
            }

            if (actividadesEspera.isEmpty()) {

                item {
                    EmptyActivitiesCard(
                        icon = Icons.Default.CheckCircle,
                        title = "No estás en ninguna lista de espera",
                        description = "Cuando una actividad esté llena podrás solicitar un lugar desde su detalle."
                    )
                }

            } else {

                items(
                    items = actividadesEspera,
                    key = { it.id }
                ) { actividad ->

                    ActividadListaEsperaCard(
                        actividad = actividad,
                        onSalir = {

                            actividadesEspera =
                                actividadesEspera.filter {
                                    it.id != actividad.id
                                }

                            if (actividad.id == actividadDisponibleId) {
                                actividadDisponibleId = null
                                mostrarNotificacion = false
                            }
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

                    FamilyDestination.INICIO ->
                        onInicioClick()

                    FamilyDestination.EXPLORAR ->
                        onExplorarClick()

                    FamilyDestination.ACTIVIDADES -> {}

                    FamilyDestination.MENSAJES ->
                        onMensajesClick()

                    FamilyDestination.PERFIL ->
                        onPerfilClick()
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

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
private fun LugarDisponibleCard(
    nombreActividad: String,
    onParticipar: () -> Unit,
    onRechazar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TealLight
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Surface),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "¡Se liberó un lugar!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Hay un lugar disponible para:",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = nombreActividad,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "¿Todavía quieres participar?",
                fontSize = 14.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = onRechazar,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "No, gracias",
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onParticipar,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {

                    Text(
                        text = "Quiero participar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActividadInscritaCard(
    actividad: ActividadInscrita
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            DateBox(
                fecha = actividad.fecha,
                backgroundColor = OrangePrimary
            )

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
                    text = "INSCRITO",
                    backgroundColor = OrangePrimary,
                    textColor = BrownPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.Schedule,
                    text = actividad.hora
                )

                Spacer(modifier = Modifier.height(5.dp))

                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = actividad.lugar
                )
            }
        }
    }
}

@Composable
private fun ActividadListaEsperaCard(
    actividad: ActividadListaEspera,
    onSalir: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                DateBox(
                    fecha = actividad.fecha,
                    backgroundColor = TealLight
                )

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
                        text = "EN LISTA DE ESPERA",
                        backgroundColor = TealLight,
                        textColor = TealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Background)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {

                    Text(
                        text = "Tu posición",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "#${actividad.posicion}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(
                icon = Icons.Default.Schedule,
                text = "${actividad.fecha} · ${actividad.hora}"
            )

            Spacer(modifier = Modifier.height(5.dp))

            InfoRow(
                icon = Icons.Default.LocationOn,
                text = actividad.lugar
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onSalir,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Salir de lista de espera",
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DateBox(
    fecha: String,
    backgroundColor: androidx.compose.ui.graphics.Color
) {

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor),
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
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
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

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = TextSecondary
        )

        Spacer(modifier = Modifier.width(6.dp))

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
        shape = RoundedCornerShape(18.dp)
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
                    .clip(CircleShape)
                    .background(TealLight),
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