package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.tec.familias.data.remote.ManagedActivityRegistrationResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarParticipantesScreen(
    registrations: List<ManagedActivityRegistrationResponse>,
    isLoading: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onCancelRegistration: (Int) -> Unit
) {
    var registrationToCancel by remember {
        mutableStateOf<ManagedActivityRegistrationResponse?>(null)
    }

    registrationToCancel?.let { registration ->
        AlertDialog(
            onDismissRequest = { registrationToCancel = null },
            title = { Text("Cancelar inscripción") },
            text = {
                Text("¿Quieres cancelar la inscripción de ${registration.family_name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        registrationToCancel = null
                        onCancelRegistration(registration.id)
                    }
                ) {
                    Text("Cancelar inscripción")
                }
            },
            dismissButton = {
                TextButton(onClick = { registrationToCancel = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar participantes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }

            !error.isNullOrBlank() -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(error, color = MaterialTheme.colorScheme.error)
                Button(onClick = onBackClick) {
                    Text("Volver")
                }
            }

            registrations.isEmpty() -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Esta actividad aún no tiene inscripciones.")
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Inscripciones: ${registrations.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(registrations, key = { it.id }) { registration ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                registration.family_name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Contacto: ${registration.contact_name}")
                            Text("Correo: ${registration.email}")
                            registration.phone?.takeIf(String::isNotBlank)?.let {
                                Text("Teléfono: $it")
                            }
                            Text("Estado: ${registration.status}")
                            Text("Personas: ${registration.people_count}")
                            if (registration.participant_names.isNotEmpty()) {
                                Text(
                                    "Integrantes: ${registration.participant_names.joinToString()}"
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { registrationToCancel = registration },
                                    enabled = !registration.status.equals(
                                        "cancelada",
                                        ignoreCase = true
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.PersonRemove,
                                        contentDescription = null
                                    )
                                    Text("  Cancelar inscripción")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
