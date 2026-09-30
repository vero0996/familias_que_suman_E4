package mx.tec.familias.ui.screens.admin.configuracion

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

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onInicioClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Campañas") },
                    label = { Text("Campañas") },
                    selected = false,
                    onClick = onCampaniasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = false,
                    onClick = onMensajesClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                    label = { Text("Configuración") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        indicatorColor = TealLight
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cuenta",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConfiguracionFila(
                icono = Icons.Default.Person,
                titulo = "Editar perfil"
            )

            Divider(color = Divider)

            ConfiguracionFila(
                icono = Icons.Default.Lock,
                titulo = "Seguridad"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Notificaciones",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = TealPrimary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Notificaciones",
                    color = TealPrimary,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = notificaciones,
                    onCheckedChange = {
                        notificaciones = it
                    }
                )
            }

            Divider(color = Divider)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TealPrimary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Recordatorios",
                    color = TealPrimary,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = recordatorios,
                    onCheckedChange = {
                        recordatorios = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ayuda",
                color = TealPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ConfiguracionFila(
                icono = Icons.Default.Help,
                titulo = "Centro de ayuda"
            )

            Divider(color = Divider)

            ConfiguracionFila(
                icono = Icons.Default.Info,
                titulo = "Acerca de"
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onCerrarSesionClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Error
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Cerrar sesión",
                    color = Error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ConfiguracionFila(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = TealPrimary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = titulo,
            color = TealPrimary,
            modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}