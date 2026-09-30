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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionMensajesCampaniaScreen(
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {}
) {
    var mensajeEnviado by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comunicación y Seguimiento",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            // Barra de navegación inferior con "Campañas" seleccionado
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Inicio"
                        )
                    },
                    label = {
                        Text("Inicio")
                    },
                    selected = false,
                    onClick = onInicioClick
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = "Campañas"
                        )
                    },
                    label = {
                        Text("Campañas")
                    },
                    selected = true,
                    onClick = onCampaniasClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        indicatorColor = TealLight
                    )
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Mensajes"
                        )
                    },
                    label = {
                        Text("Mensajes")
                    },
                    selected = false,
                    onClick = onMensajesClick
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    },
                    label = {
                        Text("Configuración")
                    },
                    selected = false,
                    onClick = onConfiguracionClick
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Tarjeta: Resumen de Inscritos
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
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

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Tarjeta Inferior Destacada: Enviar actualización urgente
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
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
                            .background(
                                Error.copy(alpha = 0.15f)
                            )
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
                        onClick = {
                            mensajeEnviado = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (mensajeEnviado) {
                                    TealPrimary
                                } else {
                                    BrownPrimary
                                }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            if (mensajeEnviado) {
                                Icons.Default.Check
                            } else {
                                Icons.AutoMirrored.Filled.Send
                            },
                            contentDescription = null,
                            tint = TextOnPrimary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text =
                                if (mensajeEnviado) {
                                    "Mensaje enviado"
                                } else {
                                    "Enviar mensaje a participantes"
                                },
                            color = TextOnPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}