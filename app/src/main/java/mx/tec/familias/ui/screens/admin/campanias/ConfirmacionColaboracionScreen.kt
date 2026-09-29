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

@Composable
fun ConfirmacionColaboracionScreen() {
    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Círculo superior con palomita de éxito
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TealLight.copy(alpha = 0.2f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(TealLight.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmado",
                        tint = TealPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título y subtítulo
            Text(
                text = "¡Colaboración\nconfirmada!",
                color = TealPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "La Asociación EcoVerde ha aceptado\ntrabajar junto a ti en esta iniciativa.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Sección de los dos logos unidos con un círculo de "+"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo Asociación Sol
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Surface)
                            .border(1.dp, Divider.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.asociacionsol), // Logo 1
                            contentDescription = "Asociación Sol",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Asociación Sol", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }

                // Círculo central con "+"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Más",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Logo EcoVerde
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Surface)
                            .border(1.dp, Divider.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eco), // Logo 2 (Asegúrate de tener este archivo)
                            contentDescription = "EcoVerde",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("EcoVerde", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Tarjeta Blanca con los detalles de la campaña
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Reforestación Urbana",
                        color = TealPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                    // Fila Fecha
                    DetalleItemExito(
                        icono = Icons.Default.CalendarToday,
                        etiqueta = "FECHA",
                        valor = "12 Dic, 2024"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila Lugar
                    DetalleItemExito(
                        icono = Icons.Default.LocationOn,
                        etiqueta = "LUGAR",
                        valor = "Parque Central, Madrid"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila Participantes con barra de progreso al 90%
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(TealLight.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PARTICIPANTES", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("45 / 50 cupos", fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                            }
                            // Porcentaje 90% en verdecito
                            Text("90%", fontSize = 12.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { 0.9f }, // 90% de progreso
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(9999.dp)),
                            color = TealLight,
                            trackColor = Divider
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Botones Inferiores
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestionar campaña", color = TextOnPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { },
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver campaña", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Función auxiliar para las filas de detalles de esta pantalla
@Composable
fun DetalleItemExito(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TealLight.copy(alpha = 0.15f))
        ) {
            Icon(icono, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Text(valor, fontSize = 14.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

