package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReutilizarCampaniaScreen(
    onBackClick: () -> Unit = {},
    onVistaPreviaClick: () -> Unit = {}
) {
    // Variables de estado para los campos de texto (con los datos de tu diseño)
    var fecha by remember { mutableStateOf("11/15/2024") }
    var horaInicio by remember { mutableStateOf("10:00 AM") }
    var horaFin by remember { mutableStateOf("02:00 PM") }
    var ubicacion by remember { mutableStateOf("Centro Cívico Norte, Calle Princip...") }
    var cupos by remember { mutableStateOf(20) }

    Scaffold(
        containerColor = Background,
        topBar = {
            // Barra superior con flecha de regreso y logo
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "Logo",
                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                        )
                        Text("Familias que Suman +", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
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
            Spacer(modifier = Modifier.height(12.dp))

            // Título principal
            Text(
                text = "Reutilizar Campaña",
                color = TealPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Modifica los detalles clave para lanzar una nueva edición de esta campaña.",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Tarjeta de Plantilla Base
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.campania_globos), // Tu imagen
                        contentDescription = "Plantilla",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(100.dp).fillMaxHeight()
                    )
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("PLANTILLA BASE", color = TealLight, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Recogida de Invierno", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Donación de ropa de abrigo para...", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Tarjeta Fecha y Horario
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fecha y Horario", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }

                    Text("Fecha de la campaña", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Divider.copy(alpha = 0.3f), // Fondo gris claro
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Hora de inicio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                            OutlinedTextField(
                                value = horaInicio,
                                onValueChange = { horaInicio = it },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary) },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Divider.copy(alpha = 0.3f),
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                            Text("Hora de fin", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                            OutlinedTextField(
                                value = horaFin,
                                onValueChange = { horaFin = it },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Divider.copy(alpha = 0.3f),
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Tarjeta Ubicación
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ubicación", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }

                    Text("Dirección o punto de encuentro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = { ubicacion = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Divider.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tarjeta Participación
            Card(
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Participación", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }

                    Text("Cupos totales de familias", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                    Text(
                        "Número máximo de familias que pueden apuntarse a esta actividad.",
                        fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Control de Cupos (Menos, Número, Más)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (cupos > 0) cupos-- },
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos", tint = TealPrimary)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 16.dp).height(40.dp).width(60.dp).background(Divider.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        ) {
                            Text(text = cupos.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary)
                        }

                        IconButton(
                            onClick = { cupos++ },
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Divider.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más", tint = TealPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Botones Finales
            Button(
                onClick = onVistaPreviaClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextOnPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver vista previa", color = TextOnPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Cancelar", color = TealPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}