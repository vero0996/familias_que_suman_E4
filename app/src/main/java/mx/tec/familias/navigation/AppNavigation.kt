package mx.tec.familias.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import mx.tec.familias.ui.auth.IntegrantesScreen
import mx.tec.familias.ui.auth.RegistroScreen
import mx.tec.familias.ui.auth.SelectorRolScreen
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

import mx.tec.familias.ui.screens.admin.dashboard.DashboardAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.MisCampaniasScreen
import mx.tec.familias.ui.screens.admin.campanias.CrearCampaniaColaborativaScreen
import mx.tec.familias.ui.screens.admin.campanias.ReutilizarCampaniaScreen
import mx.tec.familias.ui.screens.admin.campanias.VistaPreviaCampaniaScreen
import mx.tec.familias.ui.screens.admin.mensajes.MensajesAdminScreen
import mx.tec.familias.ui.screens.admin.configuracion.ConfiguracionAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionMensajesCampaniaScreen
import mx.tec.familias.ui.screens.mensajes.ChatScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val familyViewModel: FamilyViewModel = viewModel()

    var nombreUsuario by remember { mutableStateOf("") }
    var rutaDespuesDeRegistro by remember { mutableStateOf(Routes.Inicio.route) }
    var rutaDespuesDeIntegrantes by remember { mutableStateOf(Routes.Inicio.route) }

    NavHost(
        navController = navController,
        startDestination = Routes.SelectorRol.route // Pantalla inicial para el prototipo
    ) {

        // ==========================================
        // PANTALLA DE PROTOTIPO (SELECCIÓN DE ROL)
        // ==========================================
        composable(Routes.SelectorRol.route) {
            SelectorRolScreen(
                onFamiliaClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.SelectorRol.route) { inclusive = true }
                    }
                },
                onAsociacionClick = {
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.SelectorRol.route) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // RUTAS DE USUARIO FAMILIA
        // ==========================================
        composable(Routes.Inicio.route) {
            InicioScreen(
                nombreUsuario = nombreUsuario.ifEmpty { "Usuario" },
                onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                onActividadesClick = { navController.navigate(Routes.Actividades.route) },
                onPerfilClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Perfil.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Perfil.route)
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
                        navController.navigate(rutaDespuesDeRegistro) {
                            popUpTo(Routes.Registro.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.Integrantes.route) {
            IntegrantesScreen(
                viewModel = familyViewModel,
                onContinuar = {
                    navController.navigate(rutaDespuesDeIntegrantes) {
                        popUpTo(Routes.Integrantes.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Explorar.route) {
            ExplorarScreen(
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },
                onCampaniaClick = { navController.navigate(Routes.DetalleCampania.route) },
                onActividadClick = { navController.navigate(Routes.DetalleActividad.route) },
                onPerfilClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Perfil.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Perfil.route)
                    }
                }
            )
        }

        composable(Routes.DetalleCampania.route) {
            DetalleCampaniaScreen(
                onBackClick = { navController.popBackStack() },
                onParticiparClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.DetalleCampania.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.ConfirmacionCampania.route)
                    }
                }
            )
        }

        composable(Routes.ConfirmacionCampania.route) {
            ConfirmacionCampaniaScreen(
                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route) {
                        popUpTo(Routes.Explorar.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DetalleActividad.route) {
            DetalleActividadScreen(
                onBackClick = { navController.popBackStack() },
                onInscribirseClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Inscripcion.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Inscripcion.route)
                    }
                }
            )
        }

        composable(Routes.Inscripcion.route) {
            InscripcionScreen(
                viewModel = familyViewModel,
                onBackClick = { navController.popBackStack() },
                onConfirmarClick = { navController.navigate(Routes.ConfirmacionInscripcion.route) }
            )
        }

        composable(Routes.ConfirmacionInscripcion.route) {
            ConfirmacionInscripcionScreen(
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Actividades.route) {
            if (familyViewModel.usuario.value == null) {
                navController.navigate(Routes.Registro.route)
            } else {
                var mostrarMisActividades by remember { mutableStateOf(false) }
                if (mostrarMisActividades) {
                    MisActividadesScreen(
                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) { inclusive = true }
                            }
                        },
                        onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                        onPerfilClick = { navController.navigate(Routes.Perfil.route) },
                        onCalendarioClick = { mostrarMisActividades = false }
                    )
                } else {
                    CalendarioScreen(
                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) { inclusive = true }
                            }
                        },
                        onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                        onPerfilClick = { navController.navigate(Routes.Perfil.route) },
                        onActividadClick = { navController.navigate(Routes.DetalleActividad.route) },
                        onMisActividadesClick = { mostrarMisActividades = true }
                    )
                }
            }
        }

        composable(Routes.Perfil.route) {
            PerfilScreen(
                viewModel = familyViewModel,
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },
                onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                onActividadesClick = { navController.navigate(Routes.Actividades.route) },
                onAgregarIntegrante = { navController.navigate(Routes.Integrantes.route) }
            )
        }

        // ==========================================
        // RUTAS DE ADMINISTRADOR (ASOCIACIÓN)
        // ==========================================
        composable(Routes.DashboardAdmin.route) {
            DashboardAdminScreen(
                onCampaniasClick = {
                    navController.navigate(Routes.MisCampanias.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onMensajesClick = {
                    navController.navigate(Routes.MensajesAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onConfiguracionClick = {
                    navController.navigate(Routes.ConfiguracionAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onPerfilClick = { navController.navigate(Routes.ConfiguracionAdmin.route) },
                onCrearCampaniaClick = { navController.navigate(Routes.CrearCampaniaColaborativa.route) },
                onReutilizarCampaniaClick = { navController.navigate(Routes.ReutilizarCampania.route) }
            )
        }

        composable(Routes.MisCampanias.route) {
            MisCampaniasScreen(
                onInicioClick = {
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route) { inclusive = true }
                    }
                },
                onMensajesClick = {
                    navController.navigate(Routes.MensajesAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onConfiguracionClick = {
                    navController.navigate(Routes.ConfiguracionAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onNuevaCampaniaClick = { navController.navigate(Routes.CrearCampaniaColaborativa.route) },
                onGestionCampaniaClick = { navController.navigate(Routes.GestionMensajesCampania.route) }
            )
        }

        composable(Routes.CrearCampaniaColaborativa.route) {
            CrearCampaniaColaborativaScreen(
                onBackClick = { navController.popBackStack() },
                onContinuarClick = { navController.navigate(Routes.GestionMensajesCampania.route) }
            )
        }

        composable(Routes.ReutilizarCampania.route) {
            ReutilizarCampaniaScreen(
                onBackClick = { navController.popBackStack() },
                onVistaPreviaClick = { navController.navigate(Routes.VistaPreviaCampania.route) }
            )
        }

        composable(Routes.VistaPreviaCampania.route) {
            VistaPreviaCampaniaScreen(
                onBackClick = { navController.popBackStack() },
                onPublicarClick = {
                    navController.navigate(Routes.MisCampanias.route) {
                        popUpTo(Routes.MisCampanias.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MensajesAdmin.route) {
            MensajesAdminScreen(
                onInicioClick = {
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route) { inclusive = true }
                    }
                },
                onCampaniasClick = {
                    navController.navigate(Routes.MisCampanias.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onChatClick = { navController.navigate(Routes.ChatAdmin.route) },
                onConfiguracionClick = {
                    navController.navigate(Routes.ConfiguracionAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onPerfilClick = { navController.navigate(Routes.ConfiguracionAdmin.route) }
            )
        }

        composable(Routes.ChatAdmin.route) {
            ChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.ConfiguracionAdmin.route) {
            ConfiguracionAdminScreen(
                onInicioClick = {
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route) { inclusive = true }
                    }
                },
                onCampaniasClick = {
                    navController.navigate(Routes.MisCampanias.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onMensajesClick = {
                    navController.navigate(Routes.MensajesAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onCerrarSesionClick = { navController.navigate(Routes.SelectorRol.route) {
                    popUpTo(0) // Limpia todo el historial al cerrar sesión y vuelve al menú de roles
                } }
            )
        }

        composable(Routes.GestionMensajesCampania.route) {
            GestionMensajesCampaniaScreen(
                onBackClick = { navController.popBackStack() },
                onInicioClick = {
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route) { inclusive = true }
                    }
                },
                onCampaniasClick = {
                    navController.navigate(Routes.MisCampanias.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onMensajesClick = {
                    navController.navigate(Routes.MensajesAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                },
                onConfiguracionClick = {
                    navController.navigate(Routes.ConfiguracionAdmin.route) {
                        popUpTo(Routes.DashboardAdmin.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}