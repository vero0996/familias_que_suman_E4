package mx.tec.familias.ui.screens.publico

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*
import mx.tec.familias.ui.components.AdaptiveContainer
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.viewmodel.CampaignViewModel

@Composable
fun PublicoScreen(
    onIniciarSesionClick: () -> Unit
) {
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloCampaniaSeleccionada by remember { mutableStateOf("") }

    val campaignViewModel: CampaignViewModel = viewModel()
    val campaignUiState by campaignViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        campaignViewModel.loadCampaigns()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(
                color = Surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fqslogo2),
                            contentDescription = "Logo Familias que Suman",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(),
                            alignment = Alignment.CenterStart
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onIniciarSesionClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                    ) {
                        Text(
                            "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 18.dp, bottom = 32.dp, start = 18.dp, end = 18.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            text = "Familias que Suman es una iniciativa dedicada a transformar nuestra comunidad fomentando el voluntariado, la empatía y la solidaridad desde el núcleo familiar.",
                            fontSize = 14.sp, lineHeight = 20.sp, color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Beneficios de unirte", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                // Beneficios de unirte
                ProjectFixedCard(
                    nombre = "Comunidad",
                    detalle = "Sé parte de una red de familias buscando mejorar su entorno de forma continua."
                )

                ProjectFixedCard(
                    nombre = "Acceso a Voluntariados",
                    detalle = "Inscríbete en eventos, talleres y voluntariados adecuados para toda la familia."
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campañas cargadas desde el backend
                Text(
                    text = "Donaciones Activas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            PublicDonationItemCard(
                                titulo = campaign.title,
                                descripcion = campaign.description,
                                meta = campaign.organization_name,
                                imagenRes = R.drawable.comedorcomunitario,
                                onDonarClick = {
                                    tituloCampaniaSeleccionada = campaign.title
                                    mostrarDialogoDonacion = true
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nuestros Proyectos Fijos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                PublicCampaignCard(
                    title = "Red de Comedores Comunitarios",
                    description = "Operación continua de comedores que brindan más de 500 raciones diarias a personas en situación vulnerable.",
                    imagenRes = R.drawable.comedorcomunitario
                )

                Spacer(modifier = Modifier.height(16.dp))

                PublicCampaignCard(
                    title = "Centro de Apoyo Escolar",
                    description = "Espacio permanente de asesorías y donación de material didáctico para infantes de bajos recursos.",
                    imagenRes = R.drawable.utilesescolares
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (mostrarDialogoDonacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDonacion = false },
            title = {
                Text(
                    "Donar a: $tituloCampaniaSeleccionada",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text("Puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { mostrarDialogoDonacion = false }, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Entendido", color = TextOnPrimary)
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
private fun ProjectFixedCard(nombre: String, detalle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detalle, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun PublicCampaignCard(title: String, description: String, imagenRes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(painter = painterResource(id = imagenRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(22.dp)))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = description, fontSize = 14.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun PublicDonationItemCard(titulo: String, descripcion: String, meta: String, imagenRes: Int, onDonarClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(id = imagenRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(22.dp)))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = descripcion, fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = meta, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrownPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDonarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                ) {
                    Text("Donar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextOnPrimary)
                }
            }
        }
    }
}