package mx.tec.familias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mx.tec.familias.navigation.AppNavigation
import mx.tec.familias.ui.screens.admin.campanias.ConfirmacionColaboracionScreen
import mx.tec.familias.ui.screens.admin.campanias.CrearCampaniaColaborativaScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionComunicacionScreen
import mx.tec.familias.ui.screens.admin.campanias.MisCampaniasScreen
import mx.tec.familias.ui.screens.admin.campanias.ReutilizarCampaniaScreen
import mx.tec.familias.ui.screens.admin.campanias.VistaPreviaCampaniaScreen
import mx.tec.familias.ui.screens.admin.dashboard.DashboardAdminScreen
import mx.tec.familias.ui.screens.admin.mensajes.MensajesAdminScreen
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamiliasQueSumanTheme {
                MensajesAdminScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FamiliasQueSumanTheme {
        Greeting("Android")
    }
}