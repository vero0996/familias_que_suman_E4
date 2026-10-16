package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.*
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionMensajesCampaniaScreen(
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {}
) {
    var automatizacionSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeEnviado by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            AdminScreenHeader(
                title = "Comunicación y seguimiento",
                titleMaxLines = 2,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar", tint = TealPrimary)
                    }
                }
            )
        },
        bottomBar = {
            AdminBottomNavigationBar(
                currentDestination = AdminDestination.CAMPANIAS,
                onDestinationSelected = { destination ->
                    when (destination) {
                        AdminDestination.INICIO -> onInicioClick()
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Tarjeta: Resumen de Inscritos
            Card(
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    TealLight.copy(alpha = 0.2f)
                                )
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Resumen de Inscritos",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "45 / 50",
                            color = TealPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "90% Completado",
                            color = TealLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { 0.9f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(
                                RoundedCornerShape(9999.dp)
                            ),
                        color = TealLight,
                        trackColor = Divider
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "familias inscritas",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título de la sección
            Text(
                text = "Automatizaciones Activas",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "El sistema gestiona estos envíos por ti para que ahorres tiempo.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tarjeta Automatización 1: Confirmación de inscripción
            Card(
                onClick = {
                    automatizacionSeleccionada = "Confirmación de inscripción"
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                TealLight.copy(alpha = 0.2f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Confirmación de inscripción",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Enviado automáticamente",
                            color = TealLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Tarjeta Automatización 2: Recordatorio de actividad
            Card(
                onClick = {
                    automatizacionSeleccionada =
                        "Recordatorio de actividad (24h antes)"
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                OrangePrimary.copy(alpha = 0.2f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = BrownPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Recordatorio de actividad (24h antes)",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Programado",
                            color = BrownPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Tarjeta Automatización 3: Recordatorio el mismo día
            Card(
                onClick = {
                    automatizacionSeleccionada = "Recordatorio el mismo día"
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                TealLight.copy(alpha = 0.2f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Recordatorio el mismo día",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "3 horas antes",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "ON",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // 5. Tarjeta Automatización 4: Cancelación
            Card(
                onClick = {
                    automatizacionSeleccionada = "Cancelación"
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Error.copy(alpha = 0.15f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Cancelación",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Aviso inmediato",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "ON",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Tarjeta Inferior Destacada: Enviar actualización urgente

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                border = BorderStroke(
                    1.dp,
                    Divider.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Error.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Enviar actualización urgente",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Usa esta opción solo para cambios de última hora o avisos críticos.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onMensajesClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (mensajeEnviado) Success else TealPrimary
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = TextOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Enviar mensaje a participantes",
                            color = TextOnPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (automatizacionSeleccionada != null) {
            val mensaje = when (automatizacionSeleccionada) {
                "Confirmación de inscripción" ->
                    "¡Tu inscripción ha sido confirmada! Gracias por formar parte de nuestra campaña. Te compartiremos más información próximamente."

                "Recordatorio de actividad (24h antes)" ->
                    "¡Hola! Te recordamos que tienes una actividad programada para mañana. ¡Esperamos contar contigo!"

                "Recordatorio el mismo día" ->
                    "¡Hoy es el día! Te esperamos en nuestra actividad. Recuerda revisar los detalles y horarios de tu participación."

                "Cancelación" ->
                    "Te informamos que la actividad ha sido cancelada. Lamentamos los inconvenientes y te compartiremos cualquier actualización importante."

                else -> ""
            }

            AlertDialog(
                onDismissRequest = {
                    automatizacionSeleccionada = null
                    mensajeEnviado = false
                },
                title = {
                    Text(
                        text = if (mensajeEnviado) {
                            "¡Mensaje enviado!"
                        } else {
                            automatizacionSeleccionada ?: ""
                        },
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        if (mensajeEnviado) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "La simulación del envío se completó correctamente.",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "En una versión conectada al backend, aquí se confirmaría el envío real a los participantes.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "Vista previa del mensaje",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = Background,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = mensaje,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Este envío es una simulación. No se enviarán mensajes reales.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    if (mensajeEnviado) {
                        TextButton(
                            onClick = {
                                automatizacionSeleccionada = null
                                mensajeEnviado = false
                            }
                        ) {
                            Text("Aceptar", color = TealPrimary)
                        }
                    } else {
                        Button(
                            onClick = {
                                mensajeEnviado = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrownPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Simular envío")
                        }
                    }
                },
                dismissButton = {
                    if (!mensajeEnviado) {
                        TextButton(
                            onClick = {
                                automatizacionSeleccionada = null
                                mensajeEnviado = false
                            }
                        ) {
                            Text("Cancelar", color = TextSecondary)
                        }
                    }
                },
                containerColor = Surface
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Preview(showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
private fun GestionMensajesCampaniaScreenPreview() {
    FamiliasQueSumanTheme(darkTheme = false) {
        GestionMensajesCampaniaScreen()
    }
}
