package mx.tec.familias.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.R
import mx.tec.familias.ui.theme.*

@Composable
fun SelectorRolScreen(
    onFamiliaClick: () -> Unit,
    onAsociacionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de la app (Asegúrate de tener un R.drawable.icon)
        Image(
            painter = painterResource(id = R.drawable.famqsum), // Reemplaza 'tu_logo' por el nombre real de tu imagen en drawable
            contentDescription = "Logo de la organización",
            modifier = Modifier
                .height(110.dp) // Controla aquí la altura ideal para que destaque bien
                .fillMaxWidth(), // Hace que ocupe el ancho disponible de forma centrada
            contentScale = ContentScale.Fit // Mantiene las proporciones originales sin estirarse de más
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Familias que Suman +",
            color = TealPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Prototipo de Presentación\n¿Cómo deseas ingresar?",
            color = TextSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón para entrar como Familia (Usuario normal)
        Button(
            onClick = onFamiliaClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Entrar como Familia", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para entrar como Asociación (Administrador)
        Button(
            onClick = onAsociacionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrownPrimary)
        ) {
            Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Entrar como Asociación", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
