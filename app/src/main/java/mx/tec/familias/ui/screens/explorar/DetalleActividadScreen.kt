package mx.tec.familias.ui.screens.explorar

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.data.model.Activity
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun DetalleActividadScreen(
    onBackClick: () -> Unit = {},
    onInscribirseClick: () -> Unit = {},
    actividad: Activity,
    ocupados: Int,
    disponibles: Int,
    inscripcionActiva: Boolean = false,
    inscripcionConfirmada: Boolean = false,
    inscripcionesCerradas: Boolean = false,
    error: String? = null,
    onCancelarInscripcionClick: () -> Boolean
) {
    var mostrarDialogoCancelar by remember(actividad.id) { mutableStateOf(false) }
    var mostrarDialogoCompartir by remember(actividad.id) { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // URL simulada para la demostración.
    // En una versión futura puede reemplazarse por una URL real.
    val codigoActividad = remember(actividad.id) {
        ('A'..'Z').shuffled().take(3).joinToString("") +
                (100..999).random().toString()
    }

    val urlActividad = "https://familiasquesuman.app/actividad/$codigoActividad"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {

        // --------------------------------------------------
        // HEADER
        // --------------------------------------------------

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 48.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.45f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                }

                Text(
                    text = "Detalle de actividad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { mostrarDialogoCompartir = true }
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.45f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir actividad",
                            tint = TealPrimary
                        )
                    }
                }
            }
        }

        // --------------------------------------------------
        // CONTENIDO
        // --------------------------------------------------

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(22.dp))

            // --------------------------------------------------
            // IMAGEN
            // --------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.88f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.recorridainvierno),
                    contentDescription = "Imagen de la actividad",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --------------------------------------------------
            // CATEGORÍA
            // --------------------------------------------------

            Text(
                text = "VOLUNTARIADO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrownPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --------------------------------------------------
            // TÍTULO
            // --------------------------------------------------

            Text(
                text = actividad.nombre,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // --------------------------------------------------
            // ORGANIZACIÓN
            // --------------------------------------------------

            Text(
                text = actividad.organizacion,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --------------------------------------------------
            // DESCRIPCIÓN
            // --------------------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.82f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Sobre esta actividad",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = actividad.descripcion,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --------------------------------------------------
            // INFORMACIÓN
            // --------------------------------------------------

            Text(
                text = "Información de la actividad",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActivityInfoCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Fecha y hora",
                value = "${actividad.fecha} · ${actividad.hora}"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActivityInfoCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Lugar",
                value = actividad.lugar
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActivityInfoCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Participación",
                value = "$ocupados/${actividad.capacidadFamilias} familias · $disponibles lugares libres"
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        // --------------------------------------------------
        // BOTÓN INFERIOR
        // --------------------------------------------------

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.92f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (error != null) {
                    Text(
                        text = error,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (inscripcionConfirmada) {
                    OutlinedButton(
                        onClick = { mostrarDialogoCancelar = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text(
                            text = "Cancelar inscripción a la actividad",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onInscribirseClick,
                        enabled = !inscripcionActiva && !inscripcionesCerradas,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text(
                            text = when {
                                inscripcionActiva -> "Estás en lista de espera"
                                inscripcionesCerradas -> "Inscripciones cerradas"
                                disponibles == 0 -> "Unirme a lista de espera"
                                else -> "Inscribirme como Familia"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            containerColor = Surface,
            title = {
                Text(
                    text = "¿Cancelar inscripción?",
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Se cancelará la inscripción de tu familia a ${actividad.nombre}. " +
                            "Si hay familias en espera y las inscripciones siguen abiertas, el lugar se asignará a la primera."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelarInscripcionClick()
                        mostrarDialogoCancelar = false
                    }
                ) {
                    Text(
                        text = "Sí, cancelar",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCancelar = false }
                ) {
                    Text("Volver")
                }
            }
        )
    }

    // --------------------------------------------------
    // DIÁLOGO DE COMPARTIR
    // --------------------------------------------------

    if (mostrarDialogoCompartir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCompartir = false },
            containerColor = Surface,
            title = {
                Text(
                    text = "Compartir actividad",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Comparte esta actividad con otras familias:",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(TealLight.copy(alpha = 0.25f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = urlActividad,
                            fontSize = 13.sp,
                            color = TealDark
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(urlActividad))

                        Toast.makeText(
                            context,
                            "Enlace copiado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = TealPrimary
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Copiar",
                        color = TealPrimary
                    )
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"

                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "¡Mira esta actividad en Familias que Suman!\n\n" +
                                            "${actividad.nombre}\n\n" +
                                            urlActividad
                                )
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Compartir actividad"
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = TealPrimary
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Compartir",
                            color = TealPrimary
                        )
                    }

                    TextButton(
                        onClick = { mostrarDialogoCompartir = false }
                    ) {
                        Text(
                            text = "Cerrar",
                            color = TextSecondary
                        )
                    }
                }
            }
        )
    }
}

// --------------------------------------------------
// TARJETA DE INFORMACIÓN
// --------------------------------------------------

@Composable
private fun ActivityInfoCard(
    icon: @Composable () -> Unit,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.84f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(TealLight.copy(alpha = 0.60f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = value,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = TextPrimary
                )
            }
        }
    }
}