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
import mx.tec.familias.ui.screens.explorar.ExplorarScreen
import mx.tec.familias.ui.screens.explorar.DetalleCampaniaScreen
import mx.tec.familias.ui.screens.explorar.DetalleActividadScreen
import mx.tec.familias.ui.auth.IntegrantesScreen

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
                    navController.navigate(Routes.Explorar.route)
                },
                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },
                onPerfilClick = {
                    navController.navigate(Routes.Perfil.route)
                }
            )
        }

        composable(Routes.Integrantes.route) {
            IntegrantesScreen(
                onContinuar = {
                    navController.navigate(Routes.Inicio.route)
                }
            )
        }

        composable(Routes.Explorar.route) {
            ExplorarScreen(
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route)
                },
                onCampaniaClick = {
                    navController.navigate(Routes.DetalleCampania.route)
                },
                onActividadClick = {
                    navController.navigate(Routes.DetalleActividad.route)
                }
            )
        }

        composable(Routes.DetalleCampania.route) {
            DetalleCampaniaScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DetalleActividad.route) {
            DetalleActividadScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onInscribirseClick = {
                    // Después lo conectaremos con Inscripción
                }
            )
        }

        composable(Routes.Actividades.route) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("PANTALLA DE ACTIVIDADES")
            }
        }

        composable(Routes.Perfil.route) {
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