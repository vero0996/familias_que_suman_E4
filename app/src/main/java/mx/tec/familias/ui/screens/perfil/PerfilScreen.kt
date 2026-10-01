package mx.tec.familias.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ESTOS IMPORTS SON LA CLAVE PARA QUE ENCUENTRE TU BARRA INFERIOR
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.theme.*
import mx.tec.familias.viewmodel.FamilyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: FamilyViewModel,
    onInicioClick: () -> Unit,
    onExplorarClick: () -> Unit,
    onActividadesClick: () -> Unit,
    onAgregarIntegrante: () -> Unit,
    onCambiarRolClick: () -> Unit
) {
    var mostrarAcercaDe by remember { mutableStateOf(false) }
    var mostrarProximamente by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Configuración", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentDestination = FamilyDestination.PERFIL,
                mostrarMensajes = viewModel.usuario.value != null,
                onDestinationSelected = { dest ->
                    when(dest){
                        FamilyDestination.INICIO -> onInicioClick()
                        FamilyDestination.EXPLORAR -> onExplorarClick()
                        FamilyDestination.ACTIVIDADES -> onActividadesClick()
                        FamilyDestination.MENSAJES -> { /* Lógica de mensajes */ }
                        FamilyDestination.PERFIL -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Familia",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            ConfigListItem(
                icon = Icons.Default.FamilyRestroom,
                text = "Gestionar integrantes",
                onClick = onAgregarIntegrante
            )

            Divider(color = Divider, modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Cuenta",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
            )
            ConfigListItem(
                icon = Icons.Default.Person,
                text = "Editar perfil",
                onClick = { mostrarProximamente = true }
            )
            ConfigListItem(
                icon = Icons.Default.Lock,
                text = "Seguridad",
                onClick = { mostrarProximamente = true }
            )

            Divider(color = Divider, modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Ayuda",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
            )
            ConfigListItem(
                icon = Icons.Default.HelpOutline,
                text = "Centro de ayuda",
                onClick = { mostrarProximamente = true }
            )
            ConfigListItem(
                icon = Icons.Default.Info,
                text = "Acerca de Familias que Suman",
                onClick = { mostrarAcercaDe = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onCambiarRolClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                border = androidx.compose.foundation.BorderStroke(1.dp, Error.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (mostrarAcercaDe) {
            AlertDialog(
                onDismissRequest = { mostrarAcercaDe = false },
                title = { Text("Familias que Suman +", color = TealPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Somos una organización dedicada a fomentar el voluntariado y la solidaridad desde el núcleo familiar, conectando familias con causas de impacto social en nuestra comunidad.\n\nSocio Formador - Tecnológico de Monterrey.") },
                confirmButton = {
                    TextButton(onClick = { mostrarAcercaDe = false }) {
                        Text("Cerrar", color = TealPrimary)
                    }
                },
                containerColor = Surface
            )
        }

        if (mostrarProximamente) {
            AlertDialog(
                onDismissRequest = { mostrarProximamente = false },
                title = { Text("Próximamente", color = TealPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Esta función estará disponible en la siguiente versión de la aplicación.") },
                confirmButton = {
                    TextButton(onClick = { mostrarProximamente = false }) {
                        Text("Entendido", color = TealPrimary)
                    }
                },
                containerColor = Surface
            )
        }
    }
}

@Composable
private fun ConfigListItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}