package mx.tec.familias.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.tec.familias.ui.auth.RegistroScreen
import mx.tec.familias.ui.screens.inicio.InicioScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var nombreUsuario by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Routes.Registro.route
    ) {

        composable(Routes.Registro.route) {
            RegistroScreen(
                onContinuar = { nombre, registrarOtros ->
                    nombreUsuario = nombre
                    if (registrarOtros) {
                        navController.navigate(Routes.Integrantes.route)
                    } else {
                        navController.navigate(Routes.Inicio.route)
                    }
                }
            )
        }

        composable(Routes.Inicio.route) {
            InicioScreen(
                nombreUsuario = nombreUsuario.ifEmpty { "Usuario" },
                onExplorarClick = {
                    navController.navigate("explorar")
                },
                onActividadesClick = {
                    navController.navigate("actividades")
                },
                onPerfilClick = {
                    navController.navigate("perfil")
                }
            )
        }

        composable(Routes.Integrantes.route) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("PANTALLA DE INTEGRANTES")
            }
        }

        composable("explorar") {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("PANTALLA DE EXPLORAR")
            }
        }

        composable("actividades") {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("PANTALLA DE ACTIVIDADES")
            }
        }

        composable("perfil") {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("PANTALLA DE PERFIL")
            }
        }
    }
}