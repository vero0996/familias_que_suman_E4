package mx.tec.familias.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel

@Composable
fun RegistroScreen(
    viewModel: FamilyViewModel,
    onContinuar: (String, Boolean) -> Unit = { _, _ -> }
) {

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var registrarOtros by remember { mutableStateOf(false) }

    val nombreValido = nombre.trim().isNotEmpty()

    val correoValido =
        correo.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    val telefonoValido = telefono.length >= 10

    val puedeContinuar =
        nombreValido &&
                correoValido &&
                telefonoValido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "¡Bienvenido a Familias que Suman +!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registra tus datos para comenzar",
            fontSize = 15.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // NOMBRE
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nombre")
            },
            placeholder = {
                Text("Ingresa tu nombre")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CORREO
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Correo electrónico")
            },
            placeholder = {
                Text("ejemplo@correo.com")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TELÉFONO
        OutlinedTextField(
            value = telefono,
            onValueChange = { nuevoTelefono ->
                if (nuevoTelefono.all { it.isDigit() } &&
                    nuevoTelefono.length <= 10
                ) {
                    telefono = nuevoTelefono
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Teléfono")
            },
            prefix = {
                Text("+52 ")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Registrarás a otras personas de tu familia?",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // NO, SOLO YO
        RowOption(
            text = "No, solo yo",
            selected = !registrarOtros,
            onClick = {
                registrarOtros = false
            }
        )

        // SÍ, REGISTRARÉ A OTRAS PERSONAS
        RowOption(
            text = "Sí, registraré a otras personas",
            selected = registrarOtros,
            onClick = {
                registrarOtros = true
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {

                viewModel.guardarUsuario(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono
                )

                onContinuar(
                    nombre.trim(),
                    registrarOtros
                )
            }
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RowOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = text,
            color = TextPrimary,
            fontSize = 15.sp
        )
    }
}