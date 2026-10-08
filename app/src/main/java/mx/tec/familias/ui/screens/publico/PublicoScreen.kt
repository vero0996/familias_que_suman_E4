package mx.tec.familias.ui.screens.publico

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun PublicoScreen(
    onIniciarSesionClick: () -> Unit,
    onRegistrarseClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    var mostrarDialogoDonacion by remember { mutableStateOf(false) }
    var tituloCampaniaSeleccionada by remember { mutableStateOf("") }

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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fqslogo2),
                        contentDescription = "Logo Familias que Suman",
                        modifier = Modifier.height(40.dp).weight(1f, fill = false),
                        alignment = Alignment.CenterStart
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onAdminClick) {
                            Text("Admin", fontSize = 12.sp, color = TextSecondary)
                        }
                        OutlinedButton(
                            onClick = onIniciarSesionClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("LogIn", fontSize = 12.sp, color = TealPrimary)
                        }
                        Button(
                            onClick = onRegistrarseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Registrarse", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // LazyVerticalGrid se adapta automáticamente al ancho.
        // En celular será 1 columna, en horizontal o tablet serán 2.
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
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
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Campañas de Donación Activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            item {
                DonationItemCard(
                    titulo = "Sillas de Ruedas para Todos",
                    descripcion = "Apoya a adultos mayores en situación vulnerable.",
                    meta = "Meta: 15 unidades",
                    imagenRes = R.drawable.sillaruedas, // Cambiar por tu drawable
                    onDonarClick = {
                        tituloCampaniaSeleccionada = "Sillas de Ruedas"
                        mostrarDialogoDonacion = true
                    }
                )
            }
            item {
                DonationItemCard(
                    titulo = "Juguetón Familiar Navideño",
                    descripcion = "Dona juguetes en excelente estado para casas hogar.",
                    meta = "Meta: 300 juguetes",
                    imagenRes = R.drawable.juguete, // Cambiar por tu drawable
                    onDonarClick = {
                        tituloCampaniaSeleccionada = "Juguetes Navideños"
                        mostrarDialogoDonacion = true
                    }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Nuestros Proyectos Fijos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(top = 10.dp))
            }
            item {
                ProjectFixedCard(
                    nombre = "Red de Comedores Comunitarios",
                    detalle = "Operación continua de comedores que brindan más de 500 raciones diarias."
                )
            }
            item {
                ProjectFixedCard(
                    nombre = "Centro de Apoyo Escolar",
                    detalle = "Espacio permanente de asesorías y material didáctico para infantes."
                )
            }
        }

        if (mostrarDialogoDonacion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoDonacion = false },
                title = { Text("Donar a: $tituloCampaniaSeleccionada", color = TealPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Como invitado, puedes realizar tu entrega física directamente en nuestro centro de acopio principal. ¡Gracias por sumar!", color = TextSecondary) },
                confirmButton = {
                    Button(onClick = { mostrarDialogoDonacion = false }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)) {
                        Text("Entendido", color = TextOnPrimary)
                    }
                },
                containerColor = Surface
            )
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
                    Button(
                        onClick = onDonarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detalle, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}