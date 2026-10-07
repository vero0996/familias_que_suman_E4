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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@Composable
fun DashboardAdminScreen(
    tituloActividad: String,
    participantesActividad: Int,
    cuposTotalesActividad: Int,
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCrearCampaniaClick: () -> Unit = {},
    onReutilizarCampaniaClick: () -> Unit = {},
    onActividadClick: () -> Unit = {}
) {
    var mostrarAccionesUrgentes by remember {
        mutableStateOf(false)
    }

    var mostrarCancelarCampania by remember {
        mutableStateOf(false)
    }
    var mostrarDialogoCompartir by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val codigoCampania = remember {
        ('A'..'Z').shuffled().take(3).joinToString("") +
                (100..999).random().toString()
    }

    val urlCampania =
        "https://familiasquesuman.app/campana/$codigoCampania"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        // Aquí agregamos la barra de navegación inferior
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = TealPrimary, indicatorColor = TealLight)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Campañas") },
                    label = { Text("Campañas") },
                    selected = false,
                    onClick = onCampaniasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = false,
                    onClick = onMensajesClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
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
        ) {
            // TopBar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(end = 12.dp).size(28.dp)
                )
                Text(
                    text = "CommunityHub",
                    color = TealPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // FOTO DE PERFIL CIRCULAR
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil), // Asegúrate de que el archivo se llame así
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop, // Esto evita que se aplaste
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape) // Esto la recorta en círculo perfecto
                        .clickable {
                            onPerfilClick()
                        }
                )
            }

            // Contenido con scroll
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                // Saludo
                Column(modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()) {
                    Text(
                        text = "Hola, Asociación Banco\nde Alimentos",
                        color = TealPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Aquí tienes el resumen de tu impacto\nhoy.",
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
                            RoundedCornerShape(12.dp)
                        )
                        .padding(20.dp)
                ) {

                    // Contenido centrado
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
                            text = "Faltan 5 voluntarios para la campaña\n" +
                                    "\"Recogida de Invierno\" de este fin de semana.",
                            color = Error,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Menú de acciones en la esquina superior derecha
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {

                        IconButton(
                            onClick = {
                                mostrarAccionesUrgentes = true
                            },
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
                            onDismissRequest = {
                                mostrarAccionesUrgentes = false
                            }
                        ) {

                            // Compartir campaña
                            DropdownMenuItem(
                                text = {
                                    Text("Compartir campaña")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    mostrarAccionesUrgentes = false
                                    mostrarDialogoCompartir = true
                                }
                            )

                            // Enviar mensaje
                            DropdownMenuItem(
                                text = {
                                    Text("Enviar mensaje")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    mostrarAccionesUrgentes = false
                                    onMensajesClick()
                                }
                            )

                            // Cancelar campaña
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Cancelar campaña",
                                        color = Error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
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
                //----

                // Resumen de Impacto
                Text(
                    text = "Resumen de Impacto",
                    color = TealPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    // Tarjeta 1
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .border(1.dp, Divider, RoundedCornerShape(12.dp))
                            .background(Surface, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = "Campañas", tint = TealPrimary)
                        Text("3", color = TealPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text("CAMPAÑAS ACTIVAS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Tarjeta 2
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .border(1.dp, Divider, RoundedCornerShape(12.dp))
                            .background(Surface, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = BrownPrimary)
                        Text("142", color = BrownPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text("PARTICIPANTES TOTALES", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Próximas Actividades
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Próximas Actividades", color = TealPrimary, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Ver todas",
                        color = TealPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onCampaniasClick()
                        }
                    )
                }

                // Tarjeta Actividad 1 (Borde Rojo)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            onActividadClick()
                        }
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(Error))
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    tituloActividad,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Box(modifier = Modifier.background(WarningBackground, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text("URGENTE", color = Error, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(12.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sáb, 15 Nov • 10:00 AM", fontSize = 12.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Lugares disponibles", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    "$participantesActividad/$cuposTotalesActividad cupos",
                                    fontSize = 12.sp,
                                    color = Error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = {
                                    if (cuposTotalesActividad > 0) {
                                        (participantesActividad.toFloat() / cuposTotalesActividad.toFloat())
                                            .coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                color = Error,
                                trackColor = Divider
                            )
                        }
                    }
                }

                // Tarjeta Actividad 2 (Borde Teal)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            onActividadClick()
                        }
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(TealLight))
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Taller de Sensibilización Escolar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(12.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mar, 19 Nov • 09:30 AM", fontSize = 12.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Lugares disponibles", fontSize = 12.sp, color = TextSecondary)
                                Text("12/15 cupos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(progress = { 0.2f }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = TealLight, trackColor = Divider)
                        }
                    }
                }
            }
        }
    }

// DIÁLOGO DE COMPARTIR CAMPAÑA

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

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

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
                            AnnotatedString(urlCampania)
                        )

                        Toast.makeText(
                            context,
                            "Enlace copiado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.ContentCopy,
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
                                    "¡Mira esta campaña en Familias que Suman!\n\n" +
                                            "Recogida de Invierno\n\n" +
                                            urlCampania
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
                            imageVector = Icons.Default.Share,
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
}