package mx.tec.familias.ui.auth

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.Border
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

data class Integrante(
    val nombre: String,
    val edad: Int,
    val parentesco: String
)

@Composable
fun IntegrantesScreen(
    onContinuar: () -> Unit = {}
) {

    val integrantes = remember {
        mutableStateListOf<Integrante>()
    }

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var nombre by remember {
        mutableStateOf("")
    }

    var edad by remember {
        mutableStateOf("")
    }

    var parentesco by remember {
        mutableStateOf("")
    }

    val datosValidos =
        nombre.isNotBlank() &&
                edad.toIntOrNull() != null &&
                edad.toInt() >= 0 &&
                parentesco.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                )
        ) {

            Text(
                text = "Integrantes de la familia",
                fontSize = 28.sp,
                color = TealPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Agrega a las personas que forman parte de tu familia para poder inscribirlas posteriormente en actividades.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextSecondary
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

            items(integrantes) { integrante ->

                IntegranteCard(
                    integrante = integrante,
                    onDelete = {
                        integrantes.remove(integrante)
                    }
                )
            }

            item {

                if (!mostrarFormulario) {

                    OutlinedButton(
                        onClick = {
                            mostrarFormulario = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar integrante"
                        )

                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                        )

                        Text(
                            text = "Agregar integrante"
                        )
                    }

                } else {

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
                                text = "Nuevo integrante",
                                fontSize = 18.sp,
                                color = TextPrimary
                            )

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = {
                                    nombre = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Nombre")
                                },
                                singleLine = true
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            OutlinedTextField(
                                value = edad,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() }) {
                                        edad = it
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Edad")
                                },
                                singleLine = true
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            OutlinedTextField(
                                value = parentesco,
                                onValueChange = {
                                    parentesco = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text("Parentesco")
                                },
                                placeholder = {
                                    Text("Ej. Mamá, Papá, Hija...")
                                },
                                singleLine = true
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                OutlinedButton(
                                    onClick = {
                                        mostrarFormulario = false
                                        nombre = ""
                                        edad = ""
                                        parentesco = ""
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }

                                Button(
                                    onClick = {

                                        if (datosValidos) {

                                            integrantes.add(
                                                Integrante(
                                                    nombre = nombre.trim(),
                                                    edad = edad.toInt(),
                                                    parentesco = parentesco.trim()
                                                )
                                            )

                                            nombre = ""
                                            edad = ""
                                            parentesco = ""

                                            mostrarFormulario = false
                                        }
                                    },
                                    enabled = datosValidos,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TealPrimary
                                    )
                                ) {
                                    Text("Agregar")
                                }
                            }
                        }
                    }
                }
            }
        }

        // BOTÓN CONTINUAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(20.dp)
        ) {

            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = integrantes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {

                Text(
                    text = "Continuar",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun IntegranteCard(
    integrante: Integrante,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(Background)
                    .padding(12.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TealPrimary
                )
            }

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
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

            IconButton(
                onClick = onDelete
            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = TextSecondary
                )
            }
        }
    }
}