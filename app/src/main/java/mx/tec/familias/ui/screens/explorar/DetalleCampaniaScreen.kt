package mx.tec.familias.ui.screens.explorar

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun DetalleCampaniaScreen(
    onBackClick: () -> Unit = {},
    onParticiparClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = TealPrimary
                )
            }

            Text(
                text = "Detalle de campaña",
                fontSize = 20.sp,
                color = TealPrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // IMAGEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Divider)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CATEGORÍA
            Text(
                text = "EDUCACIÓN",
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = BrownPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TÍTULO
            Text(
                text = "Útiles Escolares para Todos",
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ORGANIZACIÓN
            Text(
                text = "Fundación Aprender Juntos",
                fontSize = 15.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = TealPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PROGRESO DE LA CAMPAÑA
            Text(
                text = "Progreso de la campaña",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "80% alcanzado",
                    fontSize = 15.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = TealPrimary
                )

                Text(
                    text = "Meta: 100 kits",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 0.8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = TealPrimary,
                trackColor = Divider
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "80 de 100 kits escolares reunidos",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // DESCRIPCIÓN
            Text(
                text = "Sobre esta campaña",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Esta campaña busca reunir útiles escolares para niñas y niños que los necesitan. Las familias pueden contribuir reuniendo materiales y sumándose a esta iniciativa comunitaria.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // INFORMACIÓN
            Text(
                text = "Información",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampaignInfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Fecha límite",
                value = "30 de mayo"
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampaignInfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Punto de entrega",
                value = "Centro Comunitario"
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampaignInfoRow(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Participación",
                value = "32 familias apoyando"
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        // BOTÓN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(20.dp)
        ) {

            Button(
                onClick = onParticiparClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {
                Text(
                    text = "Apoyar esta campaña",
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CampaignInfoRow(
    icon: @Composable () -> Unit,
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.padding(end = 14.dp)
        ) {
            icon()
        }

        Column {

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }
    }
}