package mx.tec.familias.ui.screens.explorar

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun DetalleActividadScreen(
    onBackClick: () -> Unit = {},
    onInscribirseClick: () -> Unit = {}
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
                .padding(
                    horizontal = 16.dp,
                    vertical = 25.dp
                ),
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
                text = "Detalle de actividad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp
                )
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // IMAGEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Divider),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.recorridainvierno), // Nombre de tu foto para esta campaña
                    contentDescription = "Imagen de la campaña",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CATEGORÍA
            Text(
                text = "VOLUNTARIADO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrownPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TÍTULO
            Text(
                text = "Plantación de Árboles en El Pardo",
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ORGANIZACIÓN
            Text(
                text = "Asociación Bosque Vivo",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SOBRE LA ACTIVIDAD
            Text(
                text = "Sobre esta actividad",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete con tu familia a una jornada de reforestación en El Pardo. Plantaremos árboles nativos y aprenderemos sobre la importancia de cuidar y conservar nuestros espacios naturales.",
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // INFORMACIÓN
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
                value = "Sábado 24 de mayo · 9:00 AM"
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
                value = "Parque El Pardo"
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
                value = "5 familias inscritas"
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        // BOTÓN FIJO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            Button(
                onClick = onInscribirseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {
                Text(
                    text = "Inscribirme como Familia",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActivityInfoCard(
    icon: @Composable () -> Unit,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                    mx.tec.familias.ui.theme.TealLight
                )
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