package mx.tec.familias.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
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
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onProfileClick: (() -> Unit)? = null,
    showProfile: Boolean = true
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarAcercaDe by remember { mutableStateOf(false) }
    var mostrarProyectos by remember { mutableStateOf(false) }

    Surface(
        color = Surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menú de 3 rayitas
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = TealPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(Surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("¿Quiénes Somos?", color = TextPrimary) },
                        onClick = { menuExpandido = false; mostrarAcercaDe = true },
                        leadingIcon = { Icon(Icons.Default.Info, tint = TealPrimary, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Proyectos Fijos", color = TextPrimary) },
                        onClick = { menuExpandido = false; mostrarProyectos = true },
                        leadingIcon = { Icon(Icons.Default.Star, tint = TealPrimary, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Ayuda y Soporte", color = TextPrimary) },
                        onClick = { menuExpandido = false },
                        leadingIcon = { Icon(Icons.Default.Help, tint = TealPrimary, contentDescription = null) }
                    )
                }
            }

            // Logo Central Integrado
            Image(
                painter = painterResource(id = R.drawable.fqslogo2),
                contentDescription = "Logo Familias que Suman",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(38.dp)
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                alignment = Alignment.CenterStart
            )

            if (showProfile && onProfileClick != null) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )
            } else {
                Spacer(modifier = Modifier.size(36.dp))
            }
        }
    }

    // Diálogos del menú
    if (mostrarAcercaDe) {
        AlertDialog(
            onDismissRequest = { mostrarAcercaDe = false },
            title = { Text("¿Quiénes Somos?", color = TealPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Familias que Suman es una iniciativa dedicada a transformar nuestra comunidad fomentando el voluntariado, la empatía y la solidaridad desde el núcleo familiar. Conectamos el esfuerzo de las familias con causas reales.", color = TextSecondary) },
            confirmButton = { TextButton(onClick = { mostrarAcercaDe = false }) { Text("Cerrar", color = TealPrimary) } },
            containerColor = Surface
        )
    }

    if (mostrarProyectos) {
        AlertDialog(
            onDismissRequest = { mostrarProyectos = false },
            title = { Text("Nuestros Proyectos Fijos", color = TealPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• Red de Comedores Comunitarios\nOperación continua de comedores que brindan más de 500 raciones diarias.\n\n• Centro de Apoyo Escolar\nEspacio permanente de asesorías y material didáctico para infantes.", color = TextSecondary)
                }
            },
            confirmButton = { TextButton(onClick = { mostrarProyectos = false }) { Text("Entendido", color = TealPrimary) } },
            containerColor = Surface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    title: String,
    onProfileClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            if (onProfileClick != null) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}