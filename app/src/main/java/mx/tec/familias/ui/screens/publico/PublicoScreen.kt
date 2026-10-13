package mx.tec.familias.ui.screens.publico

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import mx.tec.familias.ui.components.AdaptiveContainer // <-- IMPORTANTE PARA ADAPTAR A PANTALLAS
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.viewmodel.CampaignViewModel
@Composable
fun PublicoScreen(
    onIniciarSesionClick: () -> Unit, // Quitamos el valor por defecto para obligar a conectarlos
) {
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloCampaniaSeleccionada by remember { mutableStateOf("") }
    val campaignViewModel: CampaignViewModel = viewModel()
    val campaignUiState by campaignViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        campaignViewModel.loadCampaigns()
    }
    // ADAPTIVECONTAINER: Centra y adapta la vista en celulares anchos o tablets
    // AdaptiveContainer {
        Scaffold(
            containerColor = Background,
            topBar = {
                Surface(
                    color = Surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // --- AQUÍ REEMPLAZAMOS EL TEXTO POR TU LOGO ---
                        Image(
                            painter = painterResource(id = R.drawable.fqslogo2), // Usa tu logo
                            contentDescription = "Logo Familias que Suman",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(70.dp) // Altura perfecta para que no se vea ni muy grande ni muy chico
                                .weight(1f), // Hace que ocupe el espacio disponible empujando los botones
                            alignment = Alignment.CenterStart // Lo alinea a la izquierda
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onIniciarSesionClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    "Iniciar Sesión",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(TealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = TealPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("¿Quiénes Somos?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Familias que Suman es una iniciativa dedicada a transformar nuestra comunidad fomentando el voluntariado, la empatía y la solidaridad desde el núcleo familiar. Conectamos el esfuerzo de las familias con causas reales.",
                            fontSize = 14.sp, lineHeight = 20.sp, color = TextSecondary
                        )
                    }
                }

                Text("Campañas de Donación Activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                when {
                    campaignUiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TealPrimary)
                        }
                    }

                    campaignUiState.error != null -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No pudimos cargar las campañas.",
                                color = TextSecondary
                            )

                            Button(
                                onClick = { campaignViewModel.loadCampaigns() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TealPrimary
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }

                    campaignUiState.campaigns.isEmpty() -> {
                        Text(
                            text = "Por el momento no hay campañas publicadas.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    else -> {
                        campaignUiState.campaigns.forEach { campaign ->
                            DonationItemCard(
                                titulo = campaign.title,
                                descripcion = campaign.description,
                                meta = campaign.organization_name,
                                imagenRes = R.drawable.comedorcomunitario,
                                onDonarClick = {
                                    tituloCampaniaSeleccionada = campaign.title
                                    mostrarDialogoDonacion = true
                                }
                            )
                        }
                    }
                }

                Text("Nuestros Proyectos Fijos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                ProjectFixedCard(
                    nombre = "Red de Comedores Comunitarios",
                    detalle = "Operación continua de comedores que brindan más de 500 raciones diarias."
                )

                ProjectFixedCard(
                    nombre = "Centro de Apoyo Escolar",
                    detalle = "Espacio permanente de asesorías y material didáctico para infantes."
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // DIÁLOGO MOCK DE DONACIÓN
        if (mostrarDialogoDonacion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoDonacion = false },
                title = { Text("Donar a: $tituloCampaniaSeleccionada", color = TealPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Como invitado, puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = { mostrarDialogoDonacion = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Entendido", color = TextOnPrimary)
                    }
                },
                containerColor = Surface
            )
        }
    }
//}

@Composable
private fun DonationItemCard(titulo: String, descripcion: String, meta: String, imagenRes: Int, onDonarClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Image(painter = painterResource(id = imagenRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(130.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = descripcion, fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = meta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrownPrimary)
                    Button(onClick = onDonarClick, colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), modifier = Modifier.height(36.dp)) {
                        Text("Donar", fontSize = 13.sp, color = TextOnPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectFixedCard(nombre: String, detalle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detalle, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}