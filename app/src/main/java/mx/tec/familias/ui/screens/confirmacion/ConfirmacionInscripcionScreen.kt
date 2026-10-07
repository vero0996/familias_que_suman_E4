package mx.tec.familias.ui.screens.confirmacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.model.EstadoInscripcion
import mx.tec.familias.ui.screens.NotificacionLugarAsignado
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun ConfirmacionInscripcionScreen(
    onInicioClick: () -> Unit = {},
    actividad: mx.tec.familias.data.model.Activity,
    estado: mx.tec.familias.data.model.EstadoInscripcion,
    posicion: Int? = null
) {

    var mostrarNotificacion by remember {
        mutableStateOf(
            estado == EstadoInscripcion.CONFIRMADA
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ÍCONO DE ÉXITO
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(TealLight)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Estado de la solicitud",
                tint = TealPrimary,
                modifier = Modifier
                    .height(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // TÍTULO
        Text(
            text = when (estado) {
                EstadoInscripcion.CONFIRMADA -> "¡Inscripción confirmada!"
                EstadoInscripcion.EN_ESPERA -> "Estás en lista de espera"
            },
            fontSize = 28.sp,
            lineHeight = 34.sp,
            color = TealPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // MENSAJE
        Text(
            text = when (estado) {
                EstadoInscripcion.CONFIRMADA -> "Tu familia tiene un lugar confirmado."
                EstadoInscripcion.EN_ESPERA -> "Tu posición es #$posicion. Puedes consultar tu solicitud en Mis Actividades."
            },
            modifier = Modifier.fillMaxWidth(),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // RESUMEN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    androidx.compose.ui.graphics.Color.White
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = actividad.nombre,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${actividad.fecha} · ${actividad.hora}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Text(
                    text = actividad.lugar,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BOTÓN
        Button(
            onClick = onInicioClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary
            )
        ) {
            Text(
                text = "Volver al inicio",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }

    NotificacionLugarAsignado(
        mostrarNotificacion = mostrarNotificacion,
        onCerrarClick = {
            mostrarNotificacion = false
        }
    )
}   