package mx.tec.familias.ui.screens.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.components.TopBar
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.ui.theme.*

@Composable
fun InicioScreen(
    nombreUsuario: String = "damian",
    onExplorarClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    mostrarMensajes: Boolean = false,
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: () -> Unit = {},
    campaigns: List<CampaignResponse> = emptyList(),
    estadoActividades: EstadoActividades? = null
) {
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloDonacion by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopBar(
                onProfileClick = onPerfilClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentDestination = FamilyDestination.INICIO,
                mostrarMensajes = mostrarMensajes,
                onDestinationSelected = { destination ->
                    when (destination) {
                        FamilyDestination.INICIO -> Unit
                        FamilyDestination.EXPLORAR -> onExplorarClick()
                        FamilyDestination.ACTIVIDADES -> onActividadesClick()
                        FamilyDestination.MENSAJES -> onMensajesClick()
                        FamilyDestination.PERFIL -> onPerfilClick()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            item {
                Text(
                    text = "¡Hola, $nombreUsuario!",
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"La familia es el primer núcleo de solidaridad y servicio.\"",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))

                ImpactCard()

                Spacer(modifier = Modifier.height(32.dp))

                // SECCIÓN DONACIONES (Restaurada)
                Text("Donaciones Activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                DonationItemCard(
                    titulo = "Sillas de Ruedas para Todos",
                    descripcion = "Apoya a adultos mayores en situación vulnerable para mejorar su movilidad.",
                    meta = "Meta: 15 unidades",
                    imagenRes = R.drawable.comedorcomunitario, // Cambia por tu imagen real
                    onDonarClick = {
                        tituloDonacion = "Sillas de Ruedas"
                        mostrarDialogoDonacion = true
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                DonationItemCard(
                    titulo = "Juguetón Familiar Navideño",
                    descripcion = "Dona juguetes en excelente estado para llevar alegría a casas hogar.",
                    meta = "Meta: 300 juguetes",
                    imagenRes = R.drawable.utilesescolares, // Cambia por tu imagen real
                    onDonarClick = {
                        tituloDonacion = "Juguetes Navideños"
                        mostrarDialogoDonacion = true
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

            }

            // SECCIÓN CAMPAÑAS
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Campañas Recomendadas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (campaigns.isNotEmpty()) {
                items(campaigns.size) { index ->
                    val campania = campaigns[index]

                    CampaignCard(
                        tag = campania.status.uppercase(),
                        tagColor = TealDark,
                        title = campania.title,
                        organization = campania.organization_name,
                        description = campania.description,
                        progress = 0.75f,
                        progressText = "75%",
                        buttonText = "Ver campaña",
                        onClick = onCampaniaClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                item {
                    Text(
                        text = "No hay campañas disponibles por el momento.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            // SECCIÓN PROYECTOS FIJOS
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Nuestros Proyectos Fijos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Red de Comedores Comunitarios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Operación continua de comedores que brindan más de 500 raciones diarias.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Centro de Apoyo Escolar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Espacio permanente de asesorías y material didáctico para infantes.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoDonacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDonacion = false },
            title = { Text("Donar a: $tituloDonacion", color = TealPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { mostrarDialogoDonacion = false }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text("Entendido", color = TextOnPrimary)
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
private fun ImpactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(TealLight.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "♥", color = TealPrimary, fontSize = 23.sp)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = "TU IMPACTO ESTE MES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "3 Causas Apoyadas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

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
private fun CampaignCard(tag: String, tagColor: Color, title: String, organization: String, description: String, progress: Float, progressText: String, buttonText: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Image(painter = painterResource(id = R.drawable.reforestacionurbana), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(22.dp)))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, color = TealDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = description, fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(18.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(8.dp)), color = TealPrimary, trackColor = TealLight.copy(alpha = 0.35f))
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                    Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}