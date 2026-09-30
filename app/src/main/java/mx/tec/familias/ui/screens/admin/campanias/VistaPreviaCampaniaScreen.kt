package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaPreviaCampaniaScreen(
    onBackClick: () -> Unit = {},
    onPublicarClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            // Usamos CenterAlignedTopAppBar para centrar el texto "Vista Previa"
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Vista Previa",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Caja de alerta informativa (Azul clarito)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TealLight.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .background(TealLight.copy(alpha = 0.1f)) // Fondo azul muy sutil
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Toda la información anterior se ha cargado correctamente. Así es como las familias verán esta campaña.",
                    color = TealLight,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Tarjeta principal de la Vista Previa
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Contenedor de la Imagen + Etiqueta de Borrador
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.reforestacionurbana), // Usamos la foto del paso anterior
                            contentDescription = "Portada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                        // Etiqueta flotante superior derecha
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(OrangePrimary, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Borrador", color = TextOnPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Contenido textual de la tarjeta
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Reforestación en el Parque Central",
                            color = TealPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Únete a nosotros este fin de semana para revitalizar los espacios verdes de nuestra comunidad. Plantaremos...",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                        // Elementos de la lista (Fecha, Horario, Ubicación)
                        DetalleFila(
                            icono = Icons.Default.CalendarToday,
                            titulo = "Nueva Fecha",
                            valor = "Sábado, 24 de Octubre",
                            colorFondo = TealLight.copy(alpha = 0.15f),
                            colorIcono = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DetalleFila(
                            icono = Icons.Default.Schedule,
                            titulo = "Nuevo Horario",
                            valor = "09:00 AM - 13:00 PM",
                            colorFondo = OrangePrimary.copy(alpha = 0.15f),
                            colorIcono = BrownPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DetalleFila(
                            icono = Icons.Default.LocationOn,
                            titulo = "Ubicación",
                            valor = "Parque Central, Zona Norte",
                            colorFondo = TealLight.copy(alpha = 0.15f),
                            colorIcono = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Caja de Progreso (Meta de Voluntarios)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Background, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Meta de Voluntarios", fontSize = 12.sp, color = TextSecondary)
                                Text("0 / 50", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                            }
                            LinearProgressIndicator(
                                progress = { 0f }, // Está en cero como en la imagen
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(9999.dp)),
                                color = TealLight,
                                trackColor = Divider
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Botones Finales
            Button(
                onClick = onPublicarClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Campaña", color = TextOnPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seguir editando", color = TealPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Función auxiliar para crear las filas de detalles fácilmente sin repetir código
@Composable
fun DetalleFila(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    colorFondo: Color,
    colorIcono: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colorFondo)
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(titulo, color = TextSecondary, fontSize = 10.sp)
            Text(valor, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}