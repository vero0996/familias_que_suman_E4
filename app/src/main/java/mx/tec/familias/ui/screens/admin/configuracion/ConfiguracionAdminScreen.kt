package mx.tec.familias.ui.screens.admin.configuracion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionAdminScreen(
    onInicioClick: () -> Unit = {},
    onCampaniasClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onCerrarSesionClick: () -> Unit = {}
) {
    var notificaciones by remember { mutableStateOf(true) }
    var recordatorios by remember { mutableStateOf(true) }

    // Estado para controlar la ventana emergente de "Acerca de"
    var mostrarAcercaDeAdmin by remember { mutableStateOf(false) }
    var mostrarProximamente by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = { AdminScreenHeader("Configuración") },
        bottomBar = {
            AdminBottomNavigationBar(
                currentDestination = AdminDestination.CONFIGURACION,
                onDestinationSelected = { destination ->
                    when (destination) {
                        AdminDestination.INICIO -> onInicioClick()
                        AdminDestination.CAMPANIAS -> onCampaniasClick()
                        AdminDestination.MENSAJES -> onMensajesClick()
                        AdminDestination.CONFIGURACION -> Unit
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
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            ConfiguracionSeccion("Cuenta")
            ConfiguracionTarjeta {
                ConfiguracionFila(Icons.Default.Person, "Editar perfil") { mostrarProximamente = true }
                ConfiguracionSeparador()
                ConfiguracionFila(Icons.Default.Lock, "Seguridad") { mostrarProximamente = true }
            }

            Spacer(modifier = Modifier.height(20.dp))
            ConfiguracionSeccion("Notificaciones")
            ConfiguracionTarjeta {
                ConfiguracionInterruptor(
                    Icons.Default.Notifications, "Notificaciones", notificaciones
                ) { notificaciones = it }
                ConfiguracionSeparador()
                ConfiguracionInterruptor(
                    Icons.Default.Schedule, "Recordatorios", recordatorios
                ) { recordatorios = it }
            }

            Spacer(modifier = Modifier.height(20.dp))
            ConfiguracionSeccion("Ayuda")
            ConfiguracionTarjeta {
                ConfiguracionFila(Icons.Default.HelpOutline, "Centro de ayuda") { mostrarProximamente = true }
                ConfiguracionSeparador()
                ConfiguracionFila(Icons.Default.Info, "Acerca de Familias que Suman") { mostrarAcercaDeAdmin = true }
            }

            Spacer(modifier = Modifier.height(28.dp))
            OutlinedButton(
                onClick = onCerrarSesionClick,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                border = BorderStroke(1.dp, Error.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // VENTANA EMERGENTE (DIALOG) PARA "ACERCA DE" (SOCIO FORMADOR)
        if (mostrarAcercaDeAdmin) {
            AlertDialog(
                onDismissRequest = { mostrarAcercaDeAdmin = false },
                title = {
                    Text(
                        text = "Familias que Suman +",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Somos una organización socio formadora aliada con el Tecnológico de Monterrey, dedicada a fomentar el voluntariado, la participación ciudadana y la solidaridad desde el núcleo familiar, conectando activamente a las familias con causas de impacto social en nuestra comunidad.",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { mostrarAcercaDeAdmin = false }) {
                        Text("Cerrar", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Surface,
                shape = RoundedCornerShape(28.dp)
            )
        }

        // VENTANA EMERGENTE PARA FUNCIONES PRÓXIMAS (Editar perfil / Seguridad)
        if (mostrarProximamente) {
            AlertDialog(
                onDismissRequest = { mostrarProximamente = false },
                title = {
                    Text(
                        text = "Próximamente",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Esta función de administración estará disponible en la siguiente actualización del prototipo.",
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    TextButton(onClick = { mostrarProximamente = false }) {
                        Text("Entendido", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Surface,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }
}

@Composable
private fun ConfiguracionSeccion(titulo: String) {
    Text(
        titulo, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
private fun ConfiguracionTarjeta(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun ConfiguracionSeparador() {
    HorizontalDivider(
        color = Divider.copy(alpha = 0.60f),
        modifier = Modifier.padding(horizontal = 18.dp)
    )
}

@Composable
private fun ConfiguracionIcono(icono: ImageVector) {
    Box(
        modifier = Modifier.size(40.dp)
            .background(TealLight.copy(alpha = 0.60f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun ConfiguracionFila(icono: ImageVector, titulo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfiguracionIcono(icono)
        Spacer(modifier = Modifier.width(14.dp))
        Text(titulo, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null,
            tint = TextSecondary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ConfiguracionInterruptor(
    icono: ImageVector,
    titulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfiguracionIcono(icono)
        Spacer(modifier = Modifier.width(14.dp))
        Text(titulo, color = TextPrimary, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Surface,
                checkedTrackColor = TealPrimary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = TealLight.copy(alpha = 0.35f),
                uncheckedBorderColor = Border
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfiguracionAdminPreview() {
    FamiliasQueSumanTheme(darkTheme = false) { ConfiguracionAdminScreen() }
}
