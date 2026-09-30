package mx.tec.familias.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination

@Composable
fun PerfilScreen(
    viewModel: FamilyViewModel,
    onInicioClick: () -> Unit = {},
    onExplorarClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onAgregarIntegrante: () -> Unit = {}
) {

    val usuario = viewModel.usuario.value
    val integrantes = viewModel.integrantes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 50.dp,
                end = 20.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // HEADER
            item {

                Text(
                    text = "Mi perfil",
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Administra tu información y la de tu familia.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
            }

            // PERFIL PRINCIPAL
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = TealPrimary
                    ),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TealLight)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = usuario?.nombre ?: "Usuario",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Surface
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (usuario != null) {

                            ProfileInfoRow(
                                icon = Icons.Default.Email,
                                value = usuario.correo
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            ProfileInfoRow(
                                icon = Icons.Default.Phone,
                                value = "+52 ${usuario.telefono}"
                            )
                        }
                    }
                }
            }

            // FAMILIA
            item {

                Text(
                    text = "Mi familia",
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Personas que pueden acompañarte en actividades.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TealLight)
                                    .padding(11.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.FamilyRestroom,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {

                                Text(
                                    text = when (integrantes.size) {
                                        0 -> "Sin integrantes"
                                        1 -> "1 integrante"
                                        else -> "${integrantes.size} integrantes"
                                    },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Text(
                                    text = if (integrantes.isEmpty())
                                        "Todavía no has agregado familiares."
                                    else
                                        "Registrados en tu familia.",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (integrantes.isNotEmpty()) {

                            Spacer(modifier = Modifier.height(16.dp))

                            integrantes.forEachIndexed { index, integrante ->

                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(TealLight)
                                            .padding(9.dp)
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = TealPrimary,
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {

                                        Text(
                                            text = integrante.nombre,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )

                                        Text(
                                            text = "${integrante.parentesco} · ${integrante.edad} años",
                                            fontSize = 13.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedButton(
                            onClick = onAgregarIntegrante,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "+ Agregar integrante",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary
                            )
                        }
                    }
                }
            }

            // ACCIÓN PRINCIPAL
            item {

                OutlinedButton(
                    onClick = onInicioClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "Volver al inicio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimary
                    )
                }
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.PERFIL,
            mostrarMensajes = viewModel.usuario.value != null,
            onDestinationSelected = { destination ->

                when (destination) {

                    FamilyDestination.INICIO -> {
                        onInicioClick()
                    }

                    FamilyDestination.EXPLORAR -> {
                        onExplorarClick()
                    }

                    FamilyDestination.ACTIVIDADES -> {
                        onActividadesClick()
                    }

                    FamilyDestination.MENSAJES -> {
                        onMensajesClick()
                    }

                    FamilyDestination.PERFIL -> {
                        // Ya estamos en perfil
                    }
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9.dp))
                .background(TealLight)
                .padding(7.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier
                    .width(18.dp)
                    .height(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = value,
            fontSize = 14.sp,
            color = Surface
        )
    }
}