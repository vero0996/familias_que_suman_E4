package mx.tec.familias.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@Composable
fun CampaignManagementCard(
    titulo: String,
    fecha: String,
    hora: String,
    cuposOcupados: Int,
    cuposTotales: Int,
    estado: String,
    imagenId: Int // Nuevo parámetro para la foto
) {
    val progreso = cuposOcupados.toFloat() / cuposTotales.toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column {
            // 1. Imagen que abarca todo el ancho arriba
            Image(
                painter = painterResource(id = imagenId),
                contentDescription = "Portada de campaña",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Altura de la foto según Figma
            )

            // 2. Contenido de texto e iconos
            Column(modifier = Modifier.padding(16.dp)) {

                // Título y Chip de estado
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TealPrimary)

                    // Chip de "Activa" con fondo verde clarito
                    Box(
                        modifier = Modifier
                            .background(TealLight.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = estado, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = fecha, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hora
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = hora, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de progreso de cupos
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Text(text = "Cupos ocupados", fontSize = 12.sp, color = TextSecondary)
                    Text(text = cuposOcupados.toString() + "/" + cuposTotales.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(9999.dp)),
                    color = TealLight,
                    trackColor = Divider
                )

                // Línea separadora delgada
                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                // Botones redondos de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón Editar
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón Participantes
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón Actualizar/Reciclar
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
