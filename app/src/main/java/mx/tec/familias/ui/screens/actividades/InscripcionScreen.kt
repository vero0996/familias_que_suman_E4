package mx.tec.familias.ui.screens.actividades

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.FamilyMember
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
    actividad: mx.tec.familias.data.model.Activity,
    enEspera: Boolean = false,
    error: String? = null,
    onConfirmarClick: (List<String>, String) -> Unit = { _, _ -> }
) {
    val seleccionados = remember { mutableStateListOf<String>() }
    var usuarioSeleccionado by remember { mutableStateOf(true) }
    var observaciones by remember { mutableStateOf("") }

    val totalSeleccionados =
        seleccionados.size + if (usuarioSeleccionado) 1 else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {

        // HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.55f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }

                Text(
                    text = "Inscripción",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }
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
                    colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = actividad.nombre,
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
                            text = "${actividad.fecha} · ${actividad.hora}"
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
                            text = actividad.lugar
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
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    onValueChange = { observaciones = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = {
                        Text("¿Hay algo que debamos saber?")
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Surface.copy(alpha = 0.92f),
                        unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                        focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.90f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // RESUMEN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TealLight.copy(alpha = 0.55f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
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
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // BOTÓN CONFIRMAR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.92f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (error != null) {
                    Text(
                        text = error,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        val participantes = seleccionados.toList() +
                                if (usuarioSeleccionado)
                                    listOf("${viewModel.familiaId}:titular")
                                else
                                    emptyList()

                        onConfirmarClick(participantes, observaciones)
                    },
                    enabled = totalSeleccionados > 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(
                        text = if (enEspera)
                            "Unirme a lista de espera"
                        else
                            "Confirmar inscripción",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                .clip(RoundedCornerShape(14.dp))
                .background(TealLight.copy(alpha = 0.60f))
                .padding(8.dp),
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
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(TealLight.copy(alpha = 0.60f))
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
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(TealLight.copy(alpha = 0.60f))
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