package mx.tec.familias.ui.screens.inicio

import androidx.compose.foundation.Image
import mx.tec.familias.ui.components.AdaptiveContainer
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownDark
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealPrimary
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
    onCampaniaClick: () -> Unit = {} // <-- Acción para abrir el detalle de campaña / unirse
) {
    var searchText by remember { mutableStateOf("") }

    // Estado para los filtros de categorías interactivos
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    AdaptiveContainer(modifier = Modifier.background(Background)) {
            Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = 50.dp,
                    bottom = 32.dp
                )
            ) {

                item {
                    InicioHeader(
                        onProfileClick = onPerfilClick
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "¡Hola, $nombreUsuario!",
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "\"La familia es el primer núcleo de solidaridad y servicio.\"",
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ImpactCard()

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            placeholder = {
                                Text(text = "Buscar causas, asociaciones...")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = TealPrimary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // FILTROS INTERACTIVOS CONECTADOS
                        CategoryFilters(
                            filtroActual = filtroSeleccionado,
                            onFiltroChange = { nuevoFiltro -> filtroSeleccionado = nuevoFiltro }
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Causas destacadas",
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mostrar tarjetas dinámicamente según el filtro seleccionado
                        if (filtroSeleccionado == "Todos" || filtroSeleccionado == "Voluntariado" || filtroSeleccionado == "Eventos") {
                            CauseCard(
                                imageRes = R.drawable.reforestacionurbana, // Cambia por tu imagen en drawable
                                category = "MEDIO AMBIENTE",
                                title = "Reforestación Familiar",
                                organization = "Asociación Bosque Vivo",
                                description = "Únete a nuestra jornada de plantación de árboles nativos. Una excelente oportunidad para contribuir en familia.",
                                progressLabel = "Progreso de voluntarios",
                                progress = 0.80f,
                                progressText = "80%",
                                buttonText = "Unirse como Familia",
                                onButtonClick = onCampaniaClick // <-- Conectado al botón
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        if (filtroSeleccionado == "Todos" || filtroSeleccionado == "Donaciones" || filtroSeleccionado == "Talleres") {
                            CauseCard(
                                imageRes = R.drawable.utilesescolares, // Cambia por tu otra imagen en drawable
                                category = "EDUCACIÓN",
                                title = "Apoyo Escolar en el Barrio",
                                organization = "Fundación Aprender Juntos",
                                description = "Buscamos familias que quieran donar útiles escolares o dedicar 2 horas a apoyar a estudiantes.",
                                progressLabel = "Meta de donaciones",
                                progress = 0.45f,
                                progressText = "45%",
                                buttonText = "Ver detalles",
                                onButtonClick = onCampaniaClick // <-- Conectado al botón
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
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
    }
}

@Composable
private fun InicioHeader(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = TealPrimary)
        }

        Text(
            text = "Familias que Suman +",
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )

        IconButton(onClick = onProfileClick) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(TealDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun ImpactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TealPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(TealDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "♥", color = Color.White, fontSize = 23.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "TU IMPACTO ESTE MES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "3 Causas Apoyadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    filtroActual: String,
    onFiltroChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val categorias = listOf("Todos", "Voluntariado", "Donaciones", "Eventos", "Talleres")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { categoria ->
            CategoryChip(
                text = categoria,
                selected = filtroActual == categoria,
                onClick = { onFiltroChange(categoria) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(38.dp),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) OrangePrimary else Surface,
            contentColor = if (selected) BrownDark else TextSecondary
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CauseCard(
    imageRes: Int,
    category: String,
    title: String,
    organization: String,
    description: String,
    progressLabel: String,
    progress: Float,
    progressText: String,
    buttonText: String,
    onButtonClick: () -> Unit // <-- Recibe la acción del botón
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // IMAGEN REAL DE LA CAUSA
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = organization, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TealDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = description, fontSize = 14.sp, lineHeight = 21.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = progressLabel, fontSize = 12.sp, color = TextSecondary)
                    Text(text = progressText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }

                Spacer(modifier = Modifier.height(7.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = TealPrimary,
                    trackColor = Divider
                )

                Spacer(modifier = Modifier.height(18.dp))

                // BOTÓN CONECTADO
                Button(
                    onClick = onButtonClick, // <-- Ejecuta la acción al hacer clic
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}