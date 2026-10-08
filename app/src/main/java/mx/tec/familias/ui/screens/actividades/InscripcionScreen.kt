package mx.tec.familias.ui.screens.actividades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel

@Composable
fun InscripcionScreen(
    viewModel: FamilyViewModel,
    onBackClick: () -> Unit = {},
    onConfirmarClick: (
        integrantesSeleccionados: List<String>,
        observaciones: String
    ) -> Unit = { _, _ -> }
) {

    val seleccionados = remember {
        mutableStateListOf<String>()
    }

    var usuarioSeleccionado by remember {
        mutableStateOf(true)
    }

    var observaciones by remember {
        mutableStateOf("")
    }

    val totalSeleccionados =
        seleccionados.size +
                if (usuarioSeleccionado) 1 else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TealPrimary
                )
            }

            Text(
                text = "Inscripción",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
        }

        // CONTENIDO
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 4.dp,
                bottom = 20.dp
            )
        ) {

            // ACTIVIDAD
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

                        Text(
                            text = "Plantación de Árboles en El Pardo",
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            },
                            text = "Sábado 24 de mayo · 9:00 AM"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ActivityDetailRow(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            },
                            text = "Parque El Pardo"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¿Quiénes participarán?",
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Selecciona a las personas que asistirán a esta actividad.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
            }

            // MI INFORMACIÓN
            item {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mi información",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                viewModel.usuario.value?.let { usuario ->

                    UsuarioSeleccionCard(
                        nombre = usuario.nombre,
                        correo = usuario.correo,
                        seleccionado = usuarioSeleccionado,
                        onSeleccionar = {
                            usuarioSeleccionado = !usuarioSeleccionado
                        }
                    )
                }
            }

            // MIS ACOMPAÑANTES
            item {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mis acompañantes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Selecciona a las personas que te acompañarán en esta actividad.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
            }

            // LISTA DE ACOMPAÑANTES
            if (viewModel.integrantes.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Surface
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text(
                            text = "Todavía no tienes acompañantes registrados.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

            } else {

                items(viewModel.integrantes) { integrante ->

                    IntegranteSeleccionCard(
                        integrante = integrante,
                        seleccionado = seleccionados.contains(integrante.id),
                        onSeleccionar = {

                            if (seleccionados.contains(integrante.id)) {
                                seleccionados.remove(integrante.id)
                            } else {
                                seleccionados.add(integrante.id)
                            }
                        }
                    )
                }
            }

            // OBSERVACIONES
            item {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Observaciones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = {
                        observaciones = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            "¿Hay algo que debamos saber?"
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // RESUMEN
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TealLight)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TealPrimary
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "$totalSeleccionados " +
                                if (totalSeleccionados == 1)
                                    "participante seleccionado"
                                else
                                    "participantes seleccionados",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // BOTÓN CONFIRMAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .navigationBarsPadding()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {

            Button(
                onClick = {
                    onConfirmarClick(
                        seleccionados.toList(),
                        observaciones
                    )
                },
                enabled = totalSeleccionados > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Text(
                    text = "Confirmar inscripción",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActivityDetailRow(
    icon: @Composable () -> Unit,
    text: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(TealLight)
                .padding(7.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun UsuarioSeleccionCard(
    nombre: String,
    correo: String,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(TealLight)
                    .padding(9.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Yo · $correo",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Checkbox(
                checked = seleccionado,
                onCheckedChange = {
                    onSeleccionar()
                }
            )
        }
    }
}

@Composable
private fun IntegranteSeleccionCard(
    integrante: FamilyMember,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(TealLight)
                    .padding(9.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = integrante.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${integrante.parentesco} · ${integrante.edad} años",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Checkbox(
                checked = seleccionado,
                onCheckedChange = {
                    onSeleccionar()
                }
            )
        }
    }
}