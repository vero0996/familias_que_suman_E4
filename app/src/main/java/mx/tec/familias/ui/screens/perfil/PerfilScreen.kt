package mx.tec.familias.ui.screens.perfil

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.AdaptiveContainer
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
    onCambiarRolClick: () -> Unit,
    onCancelarInscripcionClick: () -> Unit = {}
) {
    var mostrarDialogoCancelacion by remember { mutableStateOf(false) }
    var estaCancelando by remember { mutableStateOf(false) }
    var mostrarAcercaDe by remember { mutableStateOf(false) }
    var mostrarProximamente by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            Card(
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 18.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Configuración",
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentDestination = FamilyDestination.PERFIL,
                mostrarMensajes = viewModel.usuario.value != null,
                onDestinationSelected = { dest ->
                    when (dest) {
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

        AdaptiveContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    "Familia",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                )

                ConfigCard {
                    ConfigListItem(
                        icon = Icons.Default.FamilyRestroom,
                        text = "Gestionar integrantes",
                        onClick = onAgregarIntegrante
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Cuenta",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                )

                ConfigCard {
                    Column {
                        ConfigListItem(
                            icon = Icons.Default.Person,
                            text = "Editar perfil",
                            onClick = { mostrarProximamente = true }
                        )

                        Divider(
                            color = Divider.copy(alpha = 0.60f),
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )

                        ConfigListItem(
                            icon = Icons.Default.Lock,
                            text = "Seguridad",
                            onClick = { mostrarProximamente = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Ayuda",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                )

                ConfigCard {
                    Column {
                        ConfigListItem(
                            icon = Icons.Default.HelpOutline,
                            text = "Centro de ayuda",
                            onClick = { mostrarProximamente = true }
                        )

                        Divider(
                            color = Divider.copy(alpha = 0.60f),
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )

                        ConfigListItem(
                            icon = Icons.Default.Info,
                            text = "Acerca de Familias que Suman",
                            onClick = { mostrarAcercaDe = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedButton(
                    onClick = onCambiarRolClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                    border = BorderStroke(1.dp, Error.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { mostrarDialogoCancelacion = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.65f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar cuenta", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (mostrarAcercaDe) {
            AlertDialog(
                onDismissRequest = { mostrarAcercaDe = false },
                title = {
                    Text(
                        "Familias que Suman +",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Somos una organización dedicada a fomentar el voluntariado y la solidaridad desde el núcleo familiar, conectando familias con causas de impacto social en nuestra comunidad.\n\nSocio Formador - Tecnológico de Monterrey."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { mostrarAcercaDe = false }) {
                        Text("Cerrar", color = TealPrimary)
                    }
                },
                containerColor = Surface,
                shape = RoundedCornerShape(28.dp)
            )
        }

        if (mostrarProximamente) {
            AlertDialog(
                onDismissRequest = { mostrarProximamente = false },
                title = {
                    Text(
                        "Próximamente",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("Esta función estará disponible en la siguiente versión de la aplicación.")
                },
                confirmButton = {
                    TextButton(onClick = { mostrarProximamente = false }) {
                        Text("Entendido", color = TealPrimary)
                    }
                },
                containerColor = Surface,
                shape = RoundedCornerShape(28.dp)
            )
        }

        if (mostrarDialogoCancelacion) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCancelacion = false },
                title = {
                    Text(
                        "¿Estás seguro?",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro de eliminar tu cuenta y la de los integrantes registrados? Esta acción cerrará tu sesión permanentemente."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarDialogoCancelacion = false
                            estaCancelando = true
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCancelacion = false }) {
                        Text("Cancelar", color = TealPrimary)
                    }
                },
                containerColor = Surface,
                shape = RoundedCornerShape(28.dp)
            )
        }
    }

    if (estaCancelando) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2500)
            onCancelarInscripcionClick()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = TealPrimary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Cancelando tu suscripción...",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        content()
    }
}

@Composable
private fun ConfigListItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    TealLight.copy(alpha = 0.60f),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

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