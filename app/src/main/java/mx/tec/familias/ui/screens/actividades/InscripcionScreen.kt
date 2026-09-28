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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel

@Composable
fun InscripcionScreen(
    viewModel: FamilyViewModel,
    onBackClick: () -> Unit = {},
    onConfirmarClick: () -> Unit = {}
) {

    // Acompañantes seleccionados
    val seleccionados = remember {
        mutableStateListOf<String>()
    }

    // La persona que hizo el registro aparece seleccionada por defecto
    var usuarioSeleccionado by remember {
        mutableStateOf(true)
    }

    var observaciones by remember {
        mutableStateOf("")
    }

    // Total de participantes seleccionados
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
                .background(Surface)
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TealPrimary
                )
            }

            Text(
                text = "Inscripción",
                fontSize = 20.sp,
                color = TextPrimary
            )
        }

        // CONTENIDO
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // INFORMACIÓN DE LA ACTIVIDAD
            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Plantación de Árboles en El Pardo",
                            fontSize = 20.sp,
                            color = TextPrimary
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Sábado 24 de mayo · 9:00 AM",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "Parque El Pardo",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "¿Quiénes participarán?",
                    fontSize = 20.sp,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Selecciona a las personas que asistirán a esta actividad.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            // MI INFORMACIÓN
            item {

                Text(
                    text = "Mi información",
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Mis acompañantes",
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Selecciona a las personas que te acompañarán en esta actividad.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            if (viewModel.integrantes.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Surface
                        ),
                        shape = RoundedCornerShape(16.dp)
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Observaciones",
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

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
                    }
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "$totalSeleccionados " +
                            if (totalSeleccionados == 1)
                                "participante seleccionado"
                            else
                                "participantes seleccionados",
                    fontSize = 15.sp,
                    color = TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }
        }

        // BOTÓN CONFIRMAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(20.dp)
        ) {

            Button(
                onClick = onConfirmarClick,
                enabled = totalSeleccionados > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Text(
                    text = "Confirmar inscripción",
                    fontSize = 16.sp
                )
            }
        }
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
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = seleccionado,
                onCheckedChange = {
                    onSeleccionar()
                }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = nombre,
                    fontSize = 16.sp,
                    color = TextPrimary
                )

                Text(
                    text = "Yo · $correo",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
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
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = seleccionado,
                onCheckedChange = {
                    onSeleccionar()
                }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = integrante.nombre,
                    fontSize = 16.sp,
                    color = TextPrimary
                )

                Text(
                    text = "${integrante.parentesco} · ${integrante.edad} años",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}