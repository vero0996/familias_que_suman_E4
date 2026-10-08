package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividadScreen(
    titulo: String,
    fecha: String,
    hora: String,
    ubicacion: String,
    participantes: Int,
    cuposTotales: Int,
    esUrgente: Boolean = false,
    imagenId: Int = R.drawable.icon,
    onBackClick: () -> Unit = {},
    onEditarClick: () -> Unit = {},
    onParticipantesClick: () -> Unit = {},
    onCompartirClick: () -> Unit = {},
    onEliminarClick: () -> Unit = {}
) {
    val progreso = participantes.toFloat() / cuposTotales.toFloat()

    Scaffold(
        containerColor = Background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de actividad",
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
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ================= IMAGEN =================

            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Imagen de la actividad",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ================= TÍTULO =================

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = titulo,
                    color = TealPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (esUrgente) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                WarningBackground,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = "URGENTE",
                            color = Error,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= INFORMACIÓN =================

            ActivityInfoRow(
                icon = Icons.Default.CalendarToday,
                text = fecha
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActivityInfoRow(
                icon = Icons.Default.Schedule,
                text = hora
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActivityInfoRow(
                icon = Icons.Default.LocationOn,
                text = ubicacion
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================= PARTICIPANTES =================

            Text(
                text = "Participantes",
                color = TealPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Divider
                ),
                modifier = Modifier.fillMaxWidth()
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

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$participantes participantes",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Text(
                            text = "$participantes / $cuposTotales",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = TealLight,
                        trackColor = Divider
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (participantes < cuposTotales) {
                            "${cuposTotales - participantes} lugares disponibles"
                        } else {
                            "Cupos llenos"
                        },
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================= ACCIONES =================

            Text(
                text = "Acciones",
                color = TealPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEditarClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Editar actividad")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onParticipantesClick,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = TealPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Gestionar participantes",
                    color = TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onCompartirClick,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = TealPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Compartir actividad",
                    color = TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = onEliminarClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Error
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Eliminar actividad",
                    color = Error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


// ======================================================
// COMPONENTE PARA INFORMACIÓN DE ACTIVIDAD
// ======================================================

@Composable
private fun ActivityInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}