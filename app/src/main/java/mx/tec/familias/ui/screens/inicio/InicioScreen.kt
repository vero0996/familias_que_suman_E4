package mx.tec.familias.ui.screens.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextOnPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun InicioScreen(
    nombreUsuario: String = "Usuario",
    onExplorarClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    mostrarMensajes: Boolean = false,
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: () -> Unit = {}
) {
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloCampaniaSeleccionada by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp)
        ) {
            item {
                InicioHeader(onProfileClick = onPerfilClick)
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

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

                    Spacer(modifier = Modifier.height(22.dp))

                    ImpactCard()

                    Spacer(modifier = Modifier.height(24.dp))

                    // ¿Quiénes Somos? Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                                text = "Familias que Suman es una iniciativa dedicada a transformar nuestra comunidad fomentando el voluntariado, la empatía y la solidaridad desde el núcleo familiar. Conectamos el esfuerzo de las familias con causas reales.",
                                fontSize = 14.sp, lineHeight = 20.sp, color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Campañas de Donación Activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    DonationItemCard(
                        titulo = "Sillas de Ruedas para Todos",
                        descripcion = "Apoya a adultos mayores en situación vulnerable.",
                        meta = "Meta: 15 unidades",
                        imagenRes = R.drawable.comedorcomunitario,
                        onDonarClick = {
                            tituloCampaniaSeleccionada = "Sillas de Ruedas"
                            mostrarDialogoDonacion = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DonationItemCard(
                        titulo = "Juguetón Familiar Navideño",
                        descripcion = "Dona juguetes en excelente estado para casas hogar.",
                        meta = "Meta: 300 juguetes",
                        imagenRes = R.drawable.utilesescolares,
                        onDonarClick = {
                            tituloCampaniaSeleccionada = "Juguetes Navideños"
                            mostrarDialogoDonacion = true
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Nuestros Proyectos Fijos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    ProjectFixedCard(
                        nombre = "Red de Comedores Comunitarios",
                        detalle = "Operación continua de comedores que brindan más de 500 raciones diarias."
                    )

                    ProjectFixedCard(
                        nombre = "Centro de Apoyo Escolar",
                        detalle = "Espacio permanente de asesorías y material didáctico para infantes."
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

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

    // DIÁLOGO MOCK DE DONACIÓN
    if (mostrarDialogoDonacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDonacion = false },
            title = { Text("Donar a: $tituloCampaniaSeleccionada", color = TealPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
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

@Composable
private fun InicioHeader(onProfileClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(TealLight.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = TealPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Text(
                text = "Familias que Suman +",
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )

            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(TealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImpactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.82f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                Text(
                    text = "♥",
                    color = TealPrimary,
                    fontSize = 23.sp
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = "TU IMPACTO ESTE MES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "3 Causas Apoyadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
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
