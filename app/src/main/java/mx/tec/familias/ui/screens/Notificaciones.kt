package mx.tec.familias.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealPrimary

@Composable
fun NotificacionLugarAsignado(
    mostrarNotificacion: Boolean,
    onCerrarClick: () -> Unit
) {

    if (mostrarNotificacion) {

        AlertDialog(
            onDismissRequest = {
                onCerrarClick()
            },

            title = {
                Text(
                    text = "Lugar asignado",
                    color = TealPrimary
                )
            },

            text = {
                Text(
                    text = "Tu lugar ha sido asignado correctamente para la actividad."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onCerrarClick()
                    }
                ) {
                    Text(
                        text = "Entendido",
                        color = TealPrimary
                    )
                }
            },

            containerColor = Surface
        )
    }
}