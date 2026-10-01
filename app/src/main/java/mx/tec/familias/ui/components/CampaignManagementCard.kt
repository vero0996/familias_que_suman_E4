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
    inscritos: Int,
    meta: Int,
    estado: String,
    onEditarClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onReutilizarClick: () -> Unit = {}
) {
    val progreso = if (meta > 0) inscritos.toFloat() / meta.toFloat() else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Portada de campaña",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TealPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(TealLight.copy(alpha = 0.2f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = estado, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = fecha, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = hora, fontSize = 14.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Text(text = "Familias inscritas", fontSize = 12.sp, color = TextSecondary)
                    Text(text = inscritos.toString() + " / " + meta.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(9999.dp)),
                    color = TealLight,
                    trackColor = Divider
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onMensajesClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Participantes", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onReutilizarClick,
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reutilizar", tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}