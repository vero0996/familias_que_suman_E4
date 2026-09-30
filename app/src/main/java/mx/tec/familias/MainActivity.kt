package mx.tec.familias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.tec.familias.navigation.AppNavigation
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FamiliasQueSumanTheme {
                AppNavigation()
            }
        }
    }
}