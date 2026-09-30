package mx.tec.familias.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.tec.familias.ui.auth.IntegrantesScreen
import mx.tec.familias.ui.auth.RegistroScreen
import mx.tec.familias.ui.screens.actividades.InscripcionScreen
import mx.tec.familias.ui.screens.explorar.DetalleActividadScreen
import mx.tec.familias.ui.screens.explorar.DetalleCampaniaScreen
import mx.tec.familias.ui.screens.explorar.ExplorarScreen
import mx.tec.familias.ui.screens.inicio.InicioScreen
import mx.tec.familias.viewmodel.FamilyViewModel
import mx.tec.familias.ui.screens.confirmacion.ConfirmacionInscripcionScreen
import mx.tec.familias.ui.screens.perfil.PerfilScreen
import mx.tec.familias.ui.screens.confirmacion.ConfirmacionCampaniaScreen
import mx.tec.familias.ui.screens.actividades.CalendarioScreen
import mx.tec.familias.ui.screens.actividades.MisActividadesScreen
import mx.tec.familias.ui.screens.mensajes.MensajesScreen
import mx.tec.familias.ui.screens.mensajes.ChatMessages
import mx.tec.familias.ui.screens.mensajes.Conversacion
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val familyViewModel: FamilyViewModel = viewModel()

    var nombreUsuario by remember {
        mutableStateOf("")
    }

    // Guarda a qué pantalla debemos ir después del registro
    var rutaDespuesDeRegistro by remember {
        mutableStateOf(Routes.Inicio.route)
    }

    var rutaDespuesDeIntegrantes by remember {
        mutableStateOf(Routes.Inicio.route)
    }

    var conversacionSeleccionada by remember {
        mutableStateOf<Conversacion?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Inicio.route
    ) {
        composable(Routes.Inicio.route) {

            InicioScreen(
                nombreUsuario = nombreUsuario.ifEmpty { "Usuario" },

                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route)
                },

                onActividadesClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Actividades.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Actividades.route)
                    }
                },

                onMensajesClick = {
                    navController.navigate(Routes.Mensajes.route)
                },

                mostrarMensajes = familyViewModel.usuario.value != null,

                onPerfilClick = {

                    // Si todavía no tiene perfil,
                    // primero lo mandamos a registro
                    if (familyViewModel.usuario.value == null) {

                        rutaDespuesDeRegistro = Routes.Perfil.route

                        navController.navigate(
                            Routes.Registro.route
                        )

                    } else {

                        navController.navigate(
                            Routes.Perfil.route
                        )
                    }
                }
            )
        }

        composable(Routes.Registro.route) {
            RegistroScreen(
                viewModel = familyViewModel,
                onContinuar = { nombre, registrarOtros ->

                    nombreUsuario = nombre

                    if (registrarOtros) {
                        navController.navigate(Routes.Integrantes.route)
                    } else {
                        navController.navigate(rutaDespuesDeRegistro)
                    }
                }
            )
        }

        composable(Routes.Integrantes.route) {
            IntegrantesScreen(
                viewModel = familyViewModel,
                onContinuar = {
                    navController.navigate(rutaDespuesDeIntegrantes)
                }
            )
        }

        composable(Routes.Explorar.route) {

            ExplorarScreen(

                onInicioClick = {
                    navController.navigate(
                        Routes.Inicio.route
                    )
                },

                onActividadesClick = {
                    navController.navigate(
                        Routes.Actividades.route
                    )
                },

                onMensajesClick = {
                    navController.navigate(
                        Routes.Mensajes.route
                    )
                },

                onCampaniaClick = {
                    navController.navigate(
                        Routes.DetalleCampania.route
                    )
                },

                onActividadClick = {
                    navController.navigate(
                        Routes.DetalleActividad.route
                    )
                },

                onPerfilClick = {

                    if (familyViewModel.usuario.value == null) {

                        rutaDespuesDeRegistro = Routes.Perfil.route

                        navController.navigate(
                            Routes.Registro.route
                        )

                    } else {

                        navController.navigate(
                            Routes.Perfil.route
                        )
                    }
                },

                mostrarMensajes = familyViewModel.usuario.value != null
            )
        }

        composable(Routes.DetalleCampania.route) {

            DetalleCampaniaScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onParticiparClick = {

                    if (familyViewModel.usuario.value == null) {

                        rutaDespuesDeRegistro =
                            Routes.DetalleCampania.route

                        navController.navigate(
                            Routes.Registro.route
                        )

                    } else {

                        navController.navigate(
                            Routes.ConfirmacionCampania.route
                        )
                    }
                }
            )
        }

        composable(Routes.ConfirmacionCampania.route) {

            ConfirmacionCampaniaScreen(
                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route) {
                        popUpTo(Routes.Explorar.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.DetalleActividad.route) {

            DetalleActividadScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onInscribirseClick = {

                    // Aquí es donde verificamos si
                    // el usuario ya tiene perfil

                    if (familyViewModel.usuario.value == null) {

                        // Guardamos que después del registro
                        // queremos ir a inscripción
                        rutaDespuesDeRegistro =
                            Routes.Inscripcion.route

                        navController.navigate(
                            Routes.Registro.route
                        )

                    } else {

                        // Si ya tiene perfil,
                        // va directo a inscripción
                        navController.navigate(
                            Routes.Inscripcion.route
                        )
                    }
                }
            )
        }

        composable(Routes.Inscripcion.route) {

            InscripcionScreen(

                viewModel = familyViewModel,

                onBackClick = {
                    navController.popBackStack()
                },

                onConfirmarClick = {

                    navController.navigate(
                        Routes.ConfirmacionInscripcion.route
                    )
                }
            )
        }

        composable(Routes.ConfirmacionInscripcion.route) {
            ConfirmacionInscripcionScreen(
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.Actividades.route) {

            if (familyViewModel.usuario.value == null) {

                navController.navigate(Routes.Registro.route)

            } else {

                var mostrarMisActividades by remember {
                    mutableStateOf(false)
                }

                if (mostrarMisActividades) {

                    MisActividadesScreen(

                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) {
                                    inclusive = true
                                }
                            }
                        },

                        onExplorarClick = {
                            navController.navigate(Routes.Explorar.route)
                        },

                        onMensajesClick = {
                            navController.navigate(Routes.Mensajes.route)
                        },

                        onPerfilClick = {
                            navController.navigate(Routes.Perfil.route)
                        },

                        onCalendarioClick = {
                            mostrarMisActividades = false
                        },

                        mostrarMensajes = familyViewModel.usuario.value != null
                    )

                } else {

                    CalendarioScreen(

                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) {
                                    inclusive = true
                                }
                            }
                        },

                        onExplorarClick = {
                            navController.navigate(Routes.Explorar.route)
                        },

                        onMensajesClick = {
                            navController.navigate(Routes.Mensajes.route)
                        },

                        onPerfilClick = {
                            navController.navigate(Routes.Perfil.route)
                        },

                        onActividadClick = {
                            navController.navigate(
                                Routes.DetalleActividad.route
                            )
                        },

                        onMisActividadesClick = {
                            mostrarMisActividades = true
                        },

                        mostrarMensajes = familyViewModel.usuario.value != null
                    )
                }
            }
        }

        composable(Routes.Mensajes.route) {

            MensajesScreen(

                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) {
                            inclusive = true
                        }
                    }
                },

                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route)
                },

                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },

                onPerfilClick = {
                    navController.navigate(Routes.Perfil.route)
                },

                onConversacionClick = { conversacion ->

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("conversacion", conversacion)

                    navController.navigate(Routes.ChatMessages.route)
                }
            )
        }

        composable(Routes.ChatMessages.route) {

            val conversacion =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Conversacion>("conversacion")

            if (conversacion != null) {

                ChatMessages(
                    conversacion = conversacion,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.Perfil.route) {
            PerfilScreen(
                viewModel = familyViewModel,

                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) {
                            inclusive = true
                        }
                    }
                },

                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route)
                },

                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },

                onMensajesClick = {
                    navController.navigate(Routes.Mensajes.route)
                },

                onAgregarIntegrante = {
                    navController.navigate(Routes.Integrantes.route)
                }
            )
        }
    }
}