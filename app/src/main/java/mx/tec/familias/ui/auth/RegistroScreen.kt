package mx.tec.familias.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.viewmodel.FamilyViewModel
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun RegistroScreen(
    viewModel: FamilyViewModel,
    onBackClick: () -> Unit = {},
    onContinuar: (String, Boolean) -> Unit = { _, _ -> }
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var cuentaCreada by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var registrarOtros by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.applicationContext) }

    val nombreValido = nombre.trim().length in 2..150

    val correoValido =
        correo.isNotBlank() &&
                correo.trim().length <= 255 &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    val telefonoValido = telefono.length >= 10

    val puedeContinuar =
        nombreValido &&
                correoValido &&
                telefonoValido &&
                contrasena.length in 8..128 &&
                contrasena == confirmacion &&
                !cargando

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {

        // BOTÓN PARA RETROCEDER A INICIO
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 18.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface),
            border = BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.90f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar a inicio",
                        tint = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Regresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier.clickable { onBackClick() }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // TÍTULO
            Text(
                text = "Crea tu perfil",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a Familias que Suman + y encuentra oportunidades para participar en tu comunidad.",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface),
                border = BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    // NOMBRE
                    OutlinedTextField(
                        value = nombre,
                        enabled = !cargando && !cuentaCreada,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Nombre")
                        },
                        placeholder = {
                            Text("Ingresa tu nombre")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.90f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // CORREO
                    OutlinedTextField(
                        value = correo,
                        enabled = !cargando && !cuentaCreada,
                        onValueChange = { correo = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Correo electrónico")
                        },
                        placeholder = {
                            Text("ejemplo@correo.com")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.90f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // TELÉFONO
                    OutlinedTextField(
                        value = telefono,
                        enabled = !cargando && !cuentaCreada,
                        onValueChange = { nuevoTelefono ->
                            if (
                                nuevoTelefono.all { it.isDigit() } &&
                                nuevoTelefono.length <= 10
                            ) {
                                telefono = nuevoTelefono
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Teléfono")
                        },
                        placeholder = {
                            Text("10 dígitos")
                        },
                        prefix = {
                            Text("+52 ")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.90f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        enabled = !cargando && !cuentaCreada,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña") },
                        supportingText = { Text("Entre 8 y 128 caracteres") },
                        isError = contrasena.isNotEmpty() && contrasena.length !in 8..128,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.90f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = confirmacion,
                        onValueChange = { confirmacion = it },
                        enabled = !cargando && !cuentaCreada,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirmar contraseña") },
                        isError = confirmacion.isNotEmpty() && contrasena != confirmacion,
                        supportingText = {
                            if (confirmacion.isNotEmpty() && contrasena != confirmacion) {
                                Text("Las contraseñas no coinciden")
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Surface.copy(alpha = 0.92f),
                            unfocusedContainerColor = Surface.copy(alpha = 0.84f),
                            focusedBorderColor = TealPrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.90f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // REGISTRAR FAMILIARES
            Text(
                text = "¿Registrarás a otras personas de tu familia?",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            RowOption(
                text = "No, solo yo",
                selected = !registrarOtros,
                onClick = {
                    registrarOtros = false
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            RowOption(
                text = "Sí, registraré a otras personas",
                selected = registrarOtros,
                onClick = {
                    registrarOtros = true
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // BOTÓN
            if (error != null) {
                Text(
                    text = error!!,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    if (!puedeContinuar) return@Button
                    cargando = true
                    error = null
                    scope.launch {
                        try {
                            if (!cuentaCreada) {
                                repository.register(nombre, correo, telefono, contrasena)
                                cuentaCreada = true
                            }
                            val token = repository.login(correo, contrasena)
                            val usuario = repository.getCurrentUser(token.access_token)
                            sessionManager.saveToken(token.access_token)
                            viewModel.guardarUsuarioAutenticado(
                                usuario.full_name, usuario.email, usuario.phone
                            )
                            viewModel.guardarSesion(token.access_token)
                            contrasena = ""
                            confirmacion = ""
                            onContinuar(usuario.full_name, registrarOtros)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            error = if (cuentaCreada) {
                                "Tu cuenta ya se creó, pero no pudimos iniciar sesión. " +
                                    "Reintenta o entra desde Iniciar sesión."
                            } else if (e is HttpException) {
                                when (e.code()) {
                                    409 -> "Ese correo ya está registrado. Inicia sesión con tu cuenta."
                                    422 -> "Revisa tus datos y la contraseña (entre 8 y 128 caracteres)."
                                    429 -> "Demasiados intentos. Intenta registrarte más tarde."
                                    else -> "No se pudo crear tu cuenta. Intenta nuevamente."
                                }
                            } else {
                                "No se pudo confirmar el registro. Revisa tu conexión; " +
                                    "si tu cuenta ya se creó, entra desde Iniciar sesión."
                            }
                        } finally {
                            cargando = false
                        }
                    }
                },
                enabled = puedeContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {
                Text(
                    text = when {
                        cargando -> "Procesando…"
                        cuentaCreada -> "Reintentar inicio de sesión"
                        else -> "Crear cuenta y continuar"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Podrás explorar campañas y actividades sin registrarte.",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RowOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Surface),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = text,
                color = TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}
