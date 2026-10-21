package mx.tec.familias.ui.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import mx.tec.familias.data.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    val correoValido =
        correo.trim().length in 5..255 &&
                Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBackClick,
            enabled = !cargando
        ) {
            Text("Volver a iniciar sesión")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Recupera tu contraseña",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        if (mensaje != null) {
            Text(
                text = mensaje!!,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                "Escribe el correo de tu cuenta para recibir " +
                        "un enlace y establecer una nueva contraseña."
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                enabled = !cargando
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (cargando || !correoValido) return@Button

                    cargando = true
                    error = null

                    scope.launch {
                        try {
                            val respuesta = repository.forgotPassword(correo)
                            mensaje = respuesta.message
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: HttpException) {
                            error = when (e.code()) {
                                429 -> "Demasiados intentos. Espera un minuto."
                                422 -> "Revisa el correo electrónico."
                                else -> "No se pudo procesar la solicitud. Intenta más tarde."
                            }
                        } catch (e: IOException) {
                            error = "No se pudo conectar con el servidor."
                        } catch (e: Exception) {
                            error = "Ocurrió un error. Intenta nuevamente."
                        } finally {
                            cargando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = correoValido && !cargando
            ) {
                Text(
                    if (cargando) "Enviando…"
                    else "Enviar enlace de recuperación"
                )
            }
        }
    }
}