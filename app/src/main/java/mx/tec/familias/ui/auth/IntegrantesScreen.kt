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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import mx.tec.familias.ui.components.AdaptiveContainer
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
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var parentesco by remember { mutableStateOf("") }

    val datosValidos =
        nombre.isNotBlank() &&
                edad.toIntOrNull() != null &&
                edad.toInt() >= 0 &&
                parentesco.isNotBlank()

    AdaptiveContainer(
        modifier = Modifier.background(
            TealLight.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TealLight.copy(alpha = 0.12f))
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
                            edad = edad,
                            parentesco = parentesco,
                            datosValidos = datosValidos,
                            onNombreChange = {
                                nombre = it
                            },
                            onEdadChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    edad = it
                                }
                            },
                            onParentescoChange = {
                                parentesco = it
                            },
                            onCancelar = {
                                mostrarFormulario = false
                                nombre = ""
                                edad = ""
                                parentesco = ""
                            },
                            onAgregar = {
                                if (datosValidos) {
                                    viewModel.agregarIntegrante(
                                        nombre = nombre,
                                        edad = edad.toInt(),
                                        parentesco = parentesco
                                    )

                                    nombre = ""
                                    edad = ""
                                    parentesco = ""
                                    mostrarFormulario = false
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
}

@Composable
private fun IntegranteCard(
    integrante: FamilyMember,
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
    edad: String,
    parentesco: String,
    datosValidos: Boolean,
    onNombreChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onParentescoChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onAgregar: () -> Unit
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
                text = "Nuevo integrante",
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = edad,
                onValueChange = onEdadChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Edad")
                },
                placeholder = {
                    Text("Ej. 12")
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

            Spacer(modifier = Modifier.height(12.dp))

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
                    enabled = datosValidos,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {
                    Text(
                        text = "Agregar",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}