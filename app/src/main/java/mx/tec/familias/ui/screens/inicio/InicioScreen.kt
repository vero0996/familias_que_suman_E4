package mx.tec.familias.ui.screens.inicio

import androidx.compose.foundation.Image
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
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
    var searchText by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inicio", fontWeight = FontWeight.Bold, color = TealPrimary) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = TealPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(TealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = TealPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Hola, $nombreUsuario",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Encuentra oportunidades para sumar en familia.",
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Barra de búsqueda estilo iOS (Gris claro redondeado)
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    placeholder = { Text(text = "Buscar causas...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Divider.copy(alpha = 0.3f),
                        unfocusedContainerColor = Divider.copy(alpha = 0.3f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                ImpactCardAppleStyle()
                Spacer(modifier = Modifier.height(28.dp))

                CategoryFilters(
                    filtroActual = filtroSeleccionado,
                    onFiltroChange = { filtroSeleccionado = it }
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Causas destacadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filtroSeleccionado == "Todos" || filtroSeleccionado == "Voluntariado" || filtroSeleccionado == "Eventos") {
                item {
                    CauseCardAppleStyle(
                        imageRes = R.drawable.reforestacionurbana, // Asegura poner tu imagen real
                        category = "MEDIO AMBIENTE",
                        title = "Reforestación Familiar",
                        organization = "Asociación Bosque Vivo",
                        progressText = "80%",
                        progress = 0.8f,
                        onButtonClick = onCampaniaClick
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (filtroSeleccionado == "Todos" || filtroSeleccionado == "Donaciones") {
                item {
                    CauseCardAppleStyle(
                        imageRes = R.drawable.utilesescolares, // Asegura poner tu imagen real
                        category = "EDUCACIÓN",
                        title = "Apoyo Escolar en el Barrio",
                        organization = "Fundación Aprender Juntos",
                        progressText = "45%",
                        progress = 0.45f,
                        onButtonClick = onCampaniaClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ImpactCardAppleStyle() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Diseño muy redondeado estilo Apple
        colors = CardDefaults.cardColors(containerColor = TealPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra suave
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(TealDark),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "♥", color = Color.White, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "TU IMPACTO", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "3 Causas Apoyadas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun CategoryFilters(filtroActual: String, onFiltroChange: (String) -> Unit) {
    val scrollState = rememberScrollState()
    val categorias = listOf("Todos", "Voluntariado", "Donaciones", "Eventos")
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categorias.forEach { categoria ->
            val isSelected = filtroActual == categoria
            Surface(
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) TextPrimary else Divider.copy(alpha = 0.4f),
                onClick = { onFiltroChange(categoria) }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = categoria,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CauseCardAppleStyle(
    imageRes: Int,
    category: String,
    title: String,
    organization: String,
    progressText: String,
    progress: Float,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Apple Cards
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = category, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrownPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = organization, fontSize = 15.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Progreso", fontSize = 13.sp, color = TextSecondary)
                    Text(text = progressText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = TealPrimary,
                    trackColor = Divider.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(text = "Ver detalles", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}