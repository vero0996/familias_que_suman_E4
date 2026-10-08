package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.ActividadAdmin
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioActividadAdminScreen(
    actividad: ActividadAdmin,
    onBackClick: () -> Unit = {},
    onGuardarClick: (ActividadAdmin) -> Unit = {}
) {

    var titulo by remember { mutableStateOf(actividad.titulo) }
    var descripcion by remember { mutableStateOf(actividad.descripcion) }
    var fecha by remember { mutableStateOf(actividad.fecha) }
    var hora by remember { mutableStateOf(actividad.hora) }
    var ubicacion by remember { mutableStateOf(actividad.ubicacion) }
    var cupos by remember { mutableStateOf(actividad.cuposTotales.toString()) }
    var esUrgente by remember { mutableStateOf(actividad.esUrgente) }

    Scaffold(
        containerColor = Background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar actividad",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Información de la actividad",
                color = TealPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Modifica los datos de la actividad y guarda los cambios.",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --------------------------------------------------
            // TÍTULO
            // --------------------------------------------------

            Text(
                text = "Título",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Ej. Recogida de Invierno")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // DESCRIPCIÓN
            // --------------------------------------------------

            Text(
                text = "Descripción",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        "Describe brevemente la actividad..."
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // FECHA
            // --------------------------------------------------

            Text(
                text = "Fecha",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = fecha,
                onValueChange = {
                    fecha = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Ej. Sáb, 15 Nov")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // HORA
            // --------------------------------------------------

            Text(
                text = "Hora",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = hora,
                onValueChange = {
                    hora = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Ej. 10:00 AM - 4:00 PM")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // UBICACIÓN
            // --------------------------------------------------

            Text(
                text = "Ubicación",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = ubicacion,
                onValueChange = {
                    ubicacion = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Ej. Centro de Acopio")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // CUPOS
            // --------------------------------------------------

            Text(
                text = "Cupos disponibles",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = cupos,
                onValueChange = { nuevoValor ->

                    if (nuevoValor.all { it.isDigit() }) {
                        cupos = nuevoValor
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Ej. 20")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------------------------------------------------
            // ACTIVIDAD URGENTE
            // --------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Actividad urgente",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Marca esta actividad como una necesidad urgente.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = esUrgente,
                    onCheckedChange = {
                        esUrgente = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --------------------------------------------------
            // GUARDAR
            // --------------------------------------------------

            Button(
                onClick = {

                    val cuposNumericos = cupos.toIntOrNull()
                        ?: actividad.cuposTotales

                    val actividadActualizada = actividad.copy(
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        fecha = fecha.trim(),
                        hora = hora.trim(),
                        ubicacion = ubicacion.trim(),
                        cuposTotales = cuposNumericos,
                        esUrgente = esUrgente
                    )

                    onGuardarClick(actividadActualizada)
                },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {

                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Guardar cambios",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackClick,

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Background,
                    contentColor = TealPrimary
                )
            ) {

                Text(
                    text = "Cancelar"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}