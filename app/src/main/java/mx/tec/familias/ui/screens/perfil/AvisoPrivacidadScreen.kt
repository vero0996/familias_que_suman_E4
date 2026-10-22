package mx.tec.familias.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvisoPrivacidadScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aviso de privacidad",
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { paddingValues ->

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(18.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                Text(
                    text = "Aviso de Privacidad Integral",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Última actualización: JULIO 2026",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextoAviso(
                    titulo = "1. Responsable del tratamiento de los datos personales",
                    texto = """
Familias que Suman es responsable del tratamiento de los datos personales que recaba y se compromete a proteger su privacidad mediante medidas administrativas, técnicas y físicas razonables para evitar su pérdida, uso indebido, acceso no autorizado, alteración o divulgación.

Para cualquier asunto relacionado con este Aviso de Privacidad puede escribir a:

hola@familiasquesuman.com
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "2. Datos personales que podemos recabar",
                    texto = """
Dependiendo de la relación que tenga con Familias que Suman, podremos solicitar:

• Nombre completo.
• Número telefónico.
• Correo electrónico.
• Ciudad o municipio de residencia.
• Información sobre participación como voluntario o donante.
• Nombre y edad de menores que participarán en actividades familiares.
• Fotografías y videos cuando exista autorización.
• Información proporcionada mediante formularios o comunicación directa.

Familias que Suman no solicita datos personales sensibles de manera habitual.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "3. Finalidades para las que utilizamos sus datos",
                    texto = """
Los datos personales podrán utilizarse para:

• Registrar familias, voluntarios y participantes.
• Coordinar campañas, proyectos y acciones solidarias.
• Dar seguimiento a inscripciones.
• Contactar a donantes, voluntarios e instituciones.
• Informar sobre cambios, horarios y ubicaciones.
• Gestionar instituciones aliadas.
• Mantener comunicación relacionada con las actividades.
• Atender dudas y solicitudes.

También podrán utilizarse para informar sobre nuevas campañas, eventos y oportunidades de voluntariado.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "4. Fotografías y videos",
                    texto = """
Durante algunas actividades podrán tomarse fotografías o videos con fines informativos, institucionales o de difusión.

Cuando sea necesario, Familias que Suman solicitará la autorización correspondiente para el uso de imagen.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "5. Transferencia de datos personales",
                    texto = """
Familias que Suman podrá compartir únicamente la información necesaria con instituciones participantes cuando sea indispensable para coordinar una actividad.

Los datos personales no serán vendidos ni compartidos con terceros para fines publicitarios.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "6. Derechos ARCO",
                    texto = """
Usted podrá ejercer sus derechos de:

• Acceso.
• Rectificación.
• Cancelación.
• Oposición.

También podrá revocar su consentimiento cuando sea legalmente procedente.

Para ejercer estos derechos puede escribir a:

hola@familiasquesuman.com
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "7. Uso de cookies y tecnologías similares",
                    texto = """
El sitio web podrá utilizar cookies u otras tecnologías para mejorar la experiencia de navegación y conocer estadísticas de uso.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "8. Conservación y seguridad de la información",
                    texto = """
Familias que Suman conservará los datos personales únicamente durante el tiempo necesario y adoptará medidas razonables para protegerlos contra acceso, uso o divulgación no autorizados.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "9. Menores de edad",
                    texto = """
La información de menores será proporcionada por quien ejerza la patria potestad o tutela y será utilizada únicamente para los fines relacionados con la actividad correspondiente.
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "10. Cambios al Aviso de Privacidad",
                    texto = """
Familias que Suman podrá modificar o actualizar este Aviso de Privacidad cuando sea necesario.

Las modificaciones serán publicadas en:

www.familiasquesuman.com
                    """.trimIndent()
                )

                TextoAviso(
                    titulo = "11. Contacto",
                    texto = """
Si tiene dudas relacionadas con el tratamiento de sus datos personales puede escribir a:

hola@familiasquesuman.com

Agradecemos la confianza depositada en Familias que Suman.
                    """.trimIndent()
                )
            }
        }
    }
}

@Composable
private fun TextoAviso(
    titulo: String,
    texto: String
) {
    Text(
        text = titulo,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TealPrimary
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = texto,
        fontSize = 14.sp,
        color = TextPrimary,
        lineHeight = 21.sp
    )

    Spacer(modifier = Modifier.height(24.dp))
}