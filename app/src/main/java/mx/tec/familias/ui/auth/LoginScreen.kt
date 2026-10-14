
package mx.tec.familias.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import mx.tec.familias.data.remote.TokenResponse
import mx.tec.familias.data.repository.AuthRepository
import mx.tec.familias.data.remote.CurrentUserResponse
import androidx.compose.ui.platform.LocalContext
import mx.tec.familias.data.remote.SessionManager

@Composable
fun LoginScreen(
    onLoginSuccess: (TokenResponse, CurrentUserResponse) -> Unit,
    onRegistroClick: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido a Familias que Suman+!",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar."
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            enabled = !cargando
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            enabled = !cargando
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (correo.isBlank() || contrasena.isBlank()) {
                    error = "Ingresa tu correo y contraseña."
                    return@Button
                }

                scope.launch {
                    cargando = true
                    error = null

                    try {

                        val token = authRepository.login(
                            email = correo.trim(),
                            password = contrasena
                        )

                        val usuario = authRepository.getCurrentUser(
                            token = token.access_token
                        )

                        sessionManager.saveToken(token.access_token)

                        onLoginSuccess(token, usuario)

                    } catch (e: HttpException) {
                        error = when (e.code()) {
                            401 -> "Correo o contraseña incorrectos."
                            429 -> "Demasiados intentos. Espera antes de volver a intentar."
                            else -> "No se pudo iniciar sesión. Código: ${e.code()}."
                        }
                    } catch (e: IOException) {
                        error = "No se pudo conectar con el servidor. Revisa tu conexión."
                    } catch (e: Exception) {
                        error = "Ocurrió un error al iniciar sesión."
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            if (cargando) {
                CircularProgressIndicator()
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onRegistroClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
