package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.BorderStroke
import mx.tec.familias.ui.components.AdaptiveContainer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCampaniaColaborativaScreen(
    onBackClick: () -> Unit = {},
    onContinuarClick: () -> Unit = {}
) {
    var buscador by remember { mutableStateOf("") }

    var ecoVerdeInvitada by remember { mutableStateOf(true) }
    var redSolidariaInvitada by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear campaña cola...",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.asociacionsol),
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            // Este es el bloque del "Resumen de Campaña" que se queda pegado abajo
            Surface(
                color = Background,
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    spotColor = Color(0x1A000000)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Tarjeta gris de resumen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Divider.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            "RESUMEN DE CAMPAÑA",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Reforestación Urbana",
                            color = TealPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Divider,
                                modifier = Modifier.size(12.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                "12 Dic, 2024",
                                color = Divider,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Divider,
                                modifier = Modifier.size(12.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                "Parque Central",
                                color = Divider,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Divider.copy(alpha = 0.5f)
                        )

                        // Fila de logos y texto de colaboración
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Imágenes encimadas
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(24.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.eco),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(
                                            1.dp,
                                            Background,
                                            CircleShape
                                        )
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.asociacionsol),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(
                                            1.dp,
                                            Background,
                                            CircleShape
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                "Asociación Sol + Asociación EcoVerde",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Continuar
                    Button(
                        onClick = onContinuarClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrownPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Continuar",
                            color = TextOnPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        AdaptiveContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Texto descriptivo superior
                Text(
                    text = "Colaborar con otra asociación permite combinar recursos y trabajar juntos en una misma iniciativa.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buscador
                OutlinedTextField(
                    value = buscador,
                    onValueChange = { buscador = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar una asociación",
                            color = Divider
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Divider,
                        focusedBorderColor = TealPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "SUGERENCIAS PARA TI",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista de Tarjetas usando nuestro componente
                AsociacionCard(
                    nombre = "Asociación EcoVerde",
                    descripcion = "Especialistas en...",
                    ubicacion = "Madrid Centro",
                    estadoInvitada = ecoVerdeInvitada,
                    logoId = R.drawable.eco,
                    onInvitarClick = {
                        ecoVerdeInvitada = !ecoVerdeInvitada
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AsociacionCard(
                    nombre = "Red Solidaria",
                    descripcion = "Apoyo vecinal y logística...",
                    ubicacion = "Vallecas, Madrid",
                    estadoInvitada = redSolidariaInvitada,
                    logoId = R.drawable.manosunidas,
                    onInvitarClick = {
                        redSolidariaInvitada = !redSolidariaInvitada
                    }
                )

                // Espacio extra al final para que el scroll no quede oculto detrás de la barra pegajosa
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// Componente reutilizable para la tarjeta de las asociaciones
@Composable
fun AsociacionCard(
    nombre: String,
    descripcion: String,
    ubicacion: String,
    estadoInvitada: Boolean,
    logoId: Int,
    onInvitarClick: () -> Unit = {}
) {
    // Configuramos los colores dependiendo de si ya fue invitada o no
    val backgroundColor =
        if (estadoInvitada) TealLight.copy(alpha = 0.1f) else Surface

    val borderColor =
        if (estadoInvitada) TealLight.copy(alpha = 0.3f) else Color.Transparent

    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (estadoInvitada) 0.dp else 2.dp
        ),
        border = BorderStroke(
            1.dp,
            borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo de la asociación
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Background)
                    .border(
                        1.dp,
                        Divider.copy(alpha = 0.3f),
                        CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = logoId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    nombre,
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    descripcion,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        ubicacion,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón (Cambia si está invitada)
            if (estadoInvitada) {
                OutlinedButton(
                    onClick = onInvitarClick,
                    border = BorderStroke(
                        1.dp,
                        TealPrimary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 0.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        "Enviada",
                        color = TealPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onInvitarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrownPrimary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 0.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        "Invitar",
                        color = TextOnPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}