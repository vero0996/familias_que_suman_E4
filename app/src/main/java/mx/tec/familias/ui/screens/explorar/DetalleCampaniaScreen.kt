package mx.tec.familias.ui.screens.explorar

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary

@Composable
fun DetalleCampaniaScreen(
    campaniaId: String = "utiles_escolares",
    onBackClick: () -> Unit = {},
    onParticiparClick: () -> Unit = {}
) {
    val esCanasta = campaniaId == "canasta_solidaria"

    val tituloCampania = if (esCanasta)
        "Canasta Solidaria de Invierno"
    else
        "Útiles Escolares para Todos"

    val categoriaCampania = if (esCanasta)
        "ALIMENTACIÓN"
    else
        "EDUCACIÓN"

    val organizacionCampania = if (esCanasta)
        "Asociación Manos Unidas"
    else
        "Fundación Aprender Juntos"

    val progresoCampania = if (esCanasta) 0.45f else 0.80f

    val progresoTexto = if (esCanasta) "45%" else "80%"

    val metaTexto = if (esCanasta)
        "Meta por definir"
    else
        "Meta: 100 kits"

    val avanceTexto = if (esCanasta)
        "45% de avance de la campaña"
    else
        "80 de 100 kits escolares reunidos"

    val descripcionCampania = if (esCanasta) {
        "Esta campaña busca apoyar a familias de la comunidad durante el invierno mediante la recolección y entrega de canastas con alimentos y productos básicos. Puedes contribuir con donaciones para ayudar a quienes más lo necesitan."
    } else {
        "Esta campaña busca reunir útiles escolares para niñas y niños que los necesitan. Las familias pueden contribuir reuniendo materiales y sumándose a esta iniciativa comunitaria."
    }

    val fechaLimite = if (esCanasta) "Por definir" else "30 de mayo"

    val puntoEntrega = if (esCanasta)
        "Centro Comunitario (por confirmar)"
    else
        "Centro Comunitario"

    val participacion = if (esCanasta)
        "Familias de la comunidad"
    else
        "32 familias apoyando"

    var mostrarDialogoCompartir by remember {
        mutableStateOf(false)
    }

    // URL simulada para esta campaña
    val codigoCampania = remember {
        ('A'..'Z').shuffled().take(3).joinToString("") +
                (100..999).random().toString()
    }

    val urlCampania = "https://familiasquesuman.app/campana/$codigoCampania"

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TealLight.copy(alpha = 0.12f))
    ) {

        // HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 48.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
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
                    text = "Detalle de campaña",
                    modifier = Modifier.weight(1f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                // BOTÓN COMPARTIR
                IconButton(onClick = {
                    // ¡AQUÍ ESTÁ EL CAMBIO PARA EL DEEP LINK!
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "¡Únete a la campaña \"$tituloCampania\" en Familias que Suman +!\n\nhttps://familiasquesuman.com/campania/$campaniaId"
                        )
                    }
                    context.startActivity(
                        Intent.createChooser(intent, "Compartir campaña")
                    )
                }) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TealLight.copy(alpha = 0.45f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir campaña",
                            tint = TealPrimary
                        )
                    }
                }
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            // IMAGEN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.comedorcomunitario),
                    contentDescription = "Imagen de la campaña",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CATEGORÍA
            Text(
                text = categoriaCampania,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrownPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TÍTULO
            Text(
                text = tituloCampania,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ORGANIZACIÓN
            Text(
                text = organizacionCampania,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PROGRESO
            Text(
                text = "Progreso de la campaña",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$progresoTexto alcanzado",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )

                        Text(
                            text = metaTexto,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progresoCampania },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = TealPrimary,
                        trackColor = TealLight.copy(alpha = 0.35f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = avanceTexto,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DESCRIPCIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Sobre esta campaña",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = descripcionCampania,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // INFORMACIÓN
            Text(
                text = "Información",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            CampaignInfoCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                },
                title = "Fecha límite",
                value = fechaLimite
            )

            Spacer(modifier = Modifier.height(10.dp))

            CampaignInfoCard(
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

            Spacer(modifier = Modifier.height(10.dp))

            CampaignInfoCard(
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

        // BOTÓN FIJO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Button(
                onClick = onParticiparClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    text = "Apoyar esta campaña",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIÁLOGO DE COMPARTIR
    if (mostrarDialogoCompartir) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCompartir = false
            },
            title = {
                Text(
                    text = "Compartir campaña"
                )
            },
            text = {
                Column {
                    Text(
                        text = "¡Invita a más personas a participar!"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tituloCampania,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Comparte esta campaña para que más personas conozcan la iniciativa."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCompartir = false

                        val urlCampania = "https://familiasquesuman.example/campania"

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "¡Únete a la campaña \"$tituloCampania\" en Familias que Suman +!\n\n$urlCampania"
                            )
                        }

                        context.startActivity(
                            Intent.createChooser(
                                intent,
                                "Compartir campaña"
                            )
                        )
                    }
                ) {
                    Text("Compartir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCompartir = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CampaignInfoCard(
    icon: @Composable () -> Unit,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
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