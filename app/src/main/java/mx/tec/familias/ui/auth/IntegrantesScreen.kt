package mx.tec.familias.ui.auth

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel

@Composable
fun IntegrantesScreen(
    viewModel: FamilyViewModel = viewModel(),
    onContinuar: () -> Unit = {}
) {
    val integrantes = viewModel.integrantes

    var mostrarFormulario by remember { mutableStateOf(false) }
    var integranteEditando by remember {
        mutableStateOf<FamilyMember?>(null)
    }
    var nombre by remember { mutableStateOf("") }
    var diaNacimiento by remember { mutableStateOf("") }
    var mesNacimiento by remember { mutableStateOf("") }
    var anioNacimiento by remember { mutableStateOf("") }
    var parentesco by remember { mutableStateOf("") }
    var errorFecha by remember { mutableStateOf<String?>(null) }

    val numeroMes = when (mesNacimiento) {
        "Enero" -> "01"
        "Febrero" -> "02"
        "Marzo" -> "03"
        "Abril" -> "04"
        "Mayo" -> "05"
        "Junio" -> "06"
        "Julio" -> "07"
        "Agosto" -> "08"
        "Septiembre" -> "09"
        "Octubre" -> "10"
        "Noviembre" -> "11"
        "Diciembre" -> "12"
        else -> ""
    }

    val fechaNacimiento =
        if (
            diaNacimiento.isNotBlank() &&
            numeroMes.isNotBlank() &&
            anioNacimiento.isNotBlank()
        ) {
            "${diaNacimiento.padStart(2, '0')}/$numeroMes/$anioNacimiento"
        } else {
            ""
        }

    val fechaValida: Boolean
    val mensajeErrorFecha: String?

    val resultadoFecha = runCatching {
        val formato = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.ROOT
        ).apply {
            isLenient = false
        }

        val posicion = java.text.ParsePosition(0)
        val fecha = formato.parse(fechaNacimiento, posicion)

        if (
            fecha == null ||
            posicion.index != fechaNacimiento.length ||
            formato.format(fecha) != fechaNacimiento
        ) {
            throw IllegalArgumentException("La fecha seleccionada no existe.")
        }

        val hoy = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.ROOT
        ).parse(
            java.text.SimpleDateFormat(
                "dd/MM/yyyy",
                java.util.Locale.ROOT
            ).format(java.util.Date())
        )!!

        if (fecha.after(hoy)) {
            throw IllegalArgumentException(
                "La fecha de nacimiento no puede ser futura."
            )
        }

        fecha
    }

    fechaValida = resultadoFecha.isSuccess
    mensajeErrorFecha = resultadoFecha.exceptionOrNull()?.message


    val datosValidos =
        nombre.isNotBlank() &&
                fechaNacimiento.isNotBlank() &&
                parentesco.isNotBlank() &&
                fechaValida

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
            .navigationBarsPadding()
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 50.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            item {
                Text(
                    text = "Integrantes de la familia",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Agrega a las personas que forman parte de tu familia para poder participar juntos en actividades.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )
            }


            viewModel.errorIntegrantes.value?.let { error ->
                item {
                    Text(
                        text = error,
                        modifier = Modifier.fillMaxWidth(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }


            // INTEGRANTES REGISTRADOS
            if (integrantes.isNotEmpty()) {
                item {
                    Text(
                        text = "Personas registradas",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(
                    items = integrantes,
                    key = { it.id }
                ) { integrante ->
                    IntegranteCard(
                        integrante = integrante,
                        onEdit = {
                            integranteEditando = integrante

                            nombre = integrante.nombre
                            parentesco = integrante.parentesco

                            val partesFecha = integrante.fechaNacimiento.split("/")

                            if (partesFecha.size == 3) {
                                diaNacimiento = partesFecha[0]
                                anioNacimiento = partesFecha[2]

                                mesNacimiento = when (partesFecha[1]) {
                                    "01" -> "Enero"
                                    "02" -> "Febrero"
                                    "03" -> "Marzo"
                                    "04" -> "Abril"
                                    "05" -> "Mayo"
                                    "06" -> "Junio"
                                    "07" -> "Julio"
                                    "08" -> "Agosto"
                                    "09" -> "Septiembre"
                                    "10" -> "Octubre"
                                    "11" -> "Noviembre"
                                    "12" -> "Diciembre"
                                    else -> ""
                                }
                            }

                            mostrarFormulario = true
                        },
                        onDelete = {
                            viewModel.eliminarIntegrante(integrante.id)
                        }
                    )
                }
            }

            // BOTÓN AGREGAR
            item {
                if (!mostrarFormulario) {
                    OutlinedButton(
                        onClick = {
                            mostrarFormulario = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            1.dp,
                            TealPrimary.copy(alpha = 0.35f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar integrante",
                            tint = TealPrimary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Agregar integrante",
                            fontSize = 15.sp,
                            color = TealPrimary
                        )
                    }
                } else {
                    NuevoIntegranteCard(
                        nombre = nombre,
                        diaNacimiento = diaNacimiento,
                        mesNacimiento = mesNacimiento,
                        anioNacimiento = anioNacimiento,
                        parentesco = parentesco,
                        datosValidos = datosValidos,
                        guardando = viewModel.guardandoIntegrante.value,
                        titulo = if (integranteEditando == null) {
                            "Nuevo integrante"
                        } else {
                            "Editar integrante"
                        },
                        textoBoton = if (integranteEditando == null) {
                            "Agregar"
                        } else {
                            "Guardar cambios"
                        },
                        mensajeErrorFecha = if (
                            diaNacimiento.isNotBlank() &&
                            mesNacimiento.isNotBlank() &&
                            anioNacimiento.isNotBlank() &&
                            !fechaValida
                        ) {
                            mensajeErrorFecha
                        } else {
                            null
                        },
                        onNombreChange = {
                            nombre = it
                        },
                        onDiaChange = {
                            diaNacimiento = it
                        },
                        onMesChange = {
                            mesNacimiento = it
                        },
                        onAnioChange = {
                            anioNacimiento = it
                        },
                        onParentescoChange = {
                            parentesco = it
                        },
                        onCancelar = {
                            mostrarFormulario = false
                            integranteEditando = null
                            nombre = ""
                            diaNacimiento = ""
                            mesNacimiento = ""
                            anioNacimiento = ""
                            parentesco = ""
                            viewModel.limpiarErrorIntegrantes()
                        },

                        onAgregar = {
                            if (datosValidos && !viewModel.guardandoIntegrante.value) {
                                val integranteActual = integranteEditando

                                if (integranteActual == null) {
                                    viewModel.agregarIntegrante(
                                        nombre = nombre,
                                        fechaNacimiento = fechaNacimiento,
                                        parentesco = parentesco,
                                        onSuccess = {
                                            nombre = ""
                                            diaNacimiento = ""
                                            mesNacimiento = ""
                                            anioNacimiento = ""
                                            parentesco = ""
                                            mostrarFormulario = false
                                            viewModel.limpiarErrorIntegrantes()
                                        }
                                    )
                                } else {
                                    viewModel.actualizarIntegrante(
                                        id = integranteActual.id,
                                        nombre = nombre,
                                        fechaNacimiento = fechaNacimiento,
                                        parentesco = parentesco,
                                        onSuccess = {
                                            nombre = ""
                                            diaNacimiento = ""
                                            mesNacimiento = ""
                                            anioNacimiento = ""
                                            parentesco = ""
                                            integranteEditando = null
                                            mostrarFormulario = false
                                            viewModel.limpiarErrorIntegrantes()
                                        }
                                    )
                                }
                            }
                        }

                    )
                }
            }

            // MENSAJE CUANDO NO HAY INTEGRANTES
            if (integrantes.isEmpty() && !mostrarFormulario) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Surface.copy(alpha = 0.88f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        TealLight.copy(alpha = 0.60f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TealPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Aún no hay integrantes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Puedes agregar a las personas de tu familia que participarán contigo.",
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }


                item {
                    viewModel.errorIntegrantes.value?.let { error ->
                        Text(
                            text = error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }

        // BOTÓN CONTINUAR
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.92f)
            ),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 12.dp),
                enabled = integrantes.isNotEmpty(),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
private fun IntegranteCard(
    integrante: FamilyMember,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface.copy(alpha = 0.88f)
        ),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        TealLight.copy(alpha = 0.60f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(11.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

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
                    text = "${integrante.parentesco} · ${integrante.fechaNacimiento}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar integrante",
                    tint = TealPrimary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar integrante",
                    tint = TextSecondary
                )
            }
        }
    }
}


@Composable
private fun NuevoIntegranteCard(
    nombre: String,
    diaNacimiento: String,
    mesNacimiento: String,
    anioNacimiento: String,
    parentesco: String,
    datosValidos: Boolean,
    guardando: Boolean,
    titulo: String,
    textoBoton: String,
    onNombreChange: (String) -> Unit,
    onDiaChange: (String) -> Unit,
    onMesChange: (String) -> Unit,
    onAnioChange: (String) -> Unit,
    onParentescoChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onAgregar: () -> Unit,
    mensajeErrorFecha: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface.copy(alpha = 0.88f)
        ),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Nombre")
                },
                placeholder = {
                    Text("Nombre completo")
                },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Surface.copy(alpha = 0.92f),
                    unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                    focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.90f)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            FechaNacimientoSelector(
                dia = diaNacimiento,
                mes = mesNacimiento,
                anio = anioNacimiento,
                onDiaChange = onDiaChange,
                onMesChange = onMesChange,
                onAnioChange = onAnioChange
            )


            if (mensajeErrorFecha != null) {
                Text(
                    text = mensajeErrorFecha,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }


            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = parentesco,
                onValueChange = onParentescoChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Parentesco")
                },
                placeholder = {
                    Text("Ej. Mamá, Papá, Hija...")
                },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Surface.copy(alpha = 0.92f),
                    unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                    focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.90f)
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 14.sp
                    )
                }


                Button(
                    onClick = onAgregar,
                    enabled = datosValidos && !guardando,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = textoBoton,
                            fontSize = 14.sp
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun FechaNacimientoSelector(
    dia: String,
    mes: String,
    anio: String,
    onDiaChange: (String) -> Unit,
    onMesChange: (String) -> Unit,
    onAnioChange: (String) -> Unit
) {
    var expandidoDia by remember { mutableStateOf(false) }
    var expandidoMes by remember { mutableStateOf(false) }
    var expandidoAnio by remember { mutableStateOf(false) }

    val dias = (1..31).map { it.toString() }
    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val anios = (2026 downTo 1900).map { it.toString() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fecha de nacimiento",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Día Dropdown
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandidoDia = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (dia.isBlank()) "Día" else dia,
                        color = if (dia.isBlank()) TextSecondary else TextPrimary,
                        fontSize = 13.sp
                    )
                }
                DropdownMenu(
                    expanded = expandidoDia,
                    onDismissRequest = { expandidoDia = false }
                ) {
                    dias.forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d) },
                            onClick = {
                                onDiaChange(d)
                                expandidoDia = false
                            }
                        )
                    }
                }
            }

            // Mes Dropdown
            Box(modifier = Modifier.weight(1.4f)) {
                OutlinedButton(
                    onClick = { expandidoMes = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (mes.isBlank()) "Mes" else mes,
                        color = if (mes.isBlank()) TextSecondary else TextPrimary,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = expandidoMes,
                    onDismissRequest = { expandidoMes = false }
                ) {
                    meses.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m) },
                            onClick = {
                                onMesChange(m)
                                expandidoMes = false
                            }
                        )
                    }
                }
            }

            // Año Dropdown
            Box(modifier = Modifier.weight(1.2f)) {
                OutlinedButton(
                    onClick = { expandidoAnio = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Surface.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (anio.isBlank()) "Año" else anio,
                        color = if (anio.isBlank()) TextSecondary else TextPrimary,
                        fontSize = 13.sp
                    )
                }
                DropdownMenu(
                    expanded = expandidoAnio,
                    onDismissRequest = { expandidoAnio = false }
                ) {
                    anios.forEach { a ->
                        DropdownMenuItem(
                            text = { Text(a) },
                            onClick = {
                                onAnioChange(a)
                                expandidoAnio = false
                            }
                        )
                    }
                }
            }
        }
    }
}
