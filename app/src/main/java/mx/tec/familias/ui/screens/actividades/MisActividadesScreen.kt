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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    val id: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String
)

data class ActividadListaEspera(
    val id: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val posicion: Int
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
    val propias = estado.inscripciones.filter { it.familiaId == familiaId }
    val confirmadas = propias.filter { it.estado == mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA }
    val espera = propias.filter { it.estado == mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA }
    Column(Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopBar(title = "Mis Actividades", showProfile = true, onProfileClick = onPerfilClick)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCalendarioClick) { Text("Calendario") }
                }
            }
            actividadesViewModel.error?.let { mensaje ->
                item { Text(mensaje, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }
            }
            item { SectionHeader("Mis actividades", "Inscripciones confirmadas de tu familia.") }
            if (confirmadas.isEmpty()) item {
                EmptyActivitiesCard(Icons.Default.EventAvailable, "No tienes actividades inscritas",
                    "Explora las actividades disponibles para participar.")
            }
            items(confirmadas, key = { "confirmada:${it.id}" }) { solicitud ->
                val evento = estado.evento(solicitud.eventoId)
                ActividadInscritaCard(
                    ActividadInscrita(solicitud.id, evento.nombre, evento.fecha, evento.hora, evento.lugar)
                )
            }
            item { SectionHeader("Lista de espera", "Respetamos el orden de llegada de las familias.") }
            if (espera.isEmpty()) item {
                EmptyActivitiesCard(Icons.Default.CheckCircle, "No estás en ninguna lista de espera",
                    "Cuando una actividad esté llena podrás solicitar un lugar desde su detalle.")
            }
            items(espera, key = { "espera:${it.id}" }) { solicitud ->
                val evento = estado.evento(solicitud.eventoId)
                ActividadListaEsperaCard(
                    ActividadListaEspera(solicitud.id, evento.nombre, evento.fecha, evento.hora,
                        evento.lugar, estado.posicion(solicitud))
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
    actividad: ActividadListaEspera
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