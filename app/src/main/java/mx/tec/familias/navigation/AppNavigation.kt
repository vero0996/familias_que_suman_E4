package mx.tec.familias.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.tec.familias.ui.screens.admin.mensajes.ChatAdminScreen
import mx.tec.familias.R
import mx.tec.familias.ui.screens.publico.PublicoScreen
import mx.tec.familias.ui.auth.LoginScreen
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
import mx.tec.familias.ui.screens.admin.campanias.MisCampaniasScreen
import mx.tec.familias.ui.screens.admin.campanias.EditarCampaniaScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionPublicacionesScreen
import mx.tec.familias.ui.screens.admin.campanias.EditarPublicacionScreen
import mx.tec.familias.viewmodel.AdminPublicationsViewModel
import mx.tec.familias.viewmodel.PublicationKind
import mx.tec.familias.viewmodel.CampaignViewModel
import androidx.activity.compose.BackHandler
import mx.tec.familias.ui.screens.admin.dashboard.DashboardAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.CrearCampaniaScreen
import mx.tec.familias.ui.screens.admin.mensajes.MensajesAdminScreen
import mx.tec.familias.ui.screens.admin.configuracion.ConfiguracionAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionMensajesCampaniaScreen
import mx.tec.familias.ui.screens.mensajes.MensajesScreen
import mx.tec.familias.ui.screens.mensajes.ChatMessages
import mx.tec.familias.ui.screens.mensajes.Conversacion
import mx.tec.familias.ui.screens.admin.campanias.DetalleActividadScreen as DetalleActividadAdminScreen
import androidx.compose.runtime.LaunchedEffect
import mx.tec.familias.data.model.EstadoParticipante
import mx.tec.familias.data.model.ParticipanteActividadAdmin
import mx.tec.familias.ui.screens.admin.campanias.GestionarParticipantesScreen
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.viewmodel.ActividadesViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val familyViewModel: FamilyViewModel = viewModel()
    val campaignViewModel: mx.tec.familias.viewmodel.CampaignViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val adminPublicationsViewModel: AdminPublicationsViewModel = viewModel(
        factory = remember(sessionManager) {
            AdminPublicationsViewModel.Factory(sessionManager)
        }
    )
    val adminPublicationsState by adminPublicationsViewModel.uiState.collectAsState()

    val actividadesViewModel: mx.tec.familias.viewmodel.ActividadesViewModel =
        viewModel(
            factory = remember(sessionManager) {
                ActividadesViewModelFactory(sessionManager)
            }
        )
    val estadoActividades = actividadesViewModel.estado
    val campaignState by campaignViewModel.uiState.collectAsState()
    val actividadSeleccionada = estadoActividades.evento(actividadesViewModel.eventoSeleccionadoId)
    val actividadRemotaSeleccionada = estadoActividades.eventoOrNull(
        actividadesViewModel.eventoSeleccionadoId
    ) ?: estadoActividades.eventos.firstOrNull()

    var nombreUsuario by remember { mutableStateOf("") }
    var rutaDespuesDeRegistro by remember { mutableStateOf(Routes.Inicio.route) }
    var rutaDespuesDeIntegrantes by remember { mutableStateOf(Routes.Inicio.route) }
    var rolUsuario by remember { mutableStateOf<String?>(null) }
    val rolesAdmin = setOf(
        "admin",
        "administrador",
        "association",
        "asociacion",
        "organizacion"
    )

    val tieneAccesoAdmin =
        !familyViewModel.accessToken.value.isNullOrBlank() &&
                rolUsuario in rolesAdmin

    var participantesAdmin by remember {
        mutableStateOf(
            listOf(
                ParticipanteActividadAdmin(
                    id = 1,
                    nombreFamilia = "Familia González",
                    correo = "gonzalez@email.com",
                    telefono = "81 1234 5678",
                    integrantes = listOf(
                        "Alberto",
                        "Elena",
                        "Lucía",
                        "Mateo"
                    ),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(1, 2, 3, 4)
                ),

                ParticipanteActividadAdmin(
                    id = 2,
                    nombreFamilia = "Familia Martínez",
                    correo = "martinez@email.com",
                    telefono = "81 2345 6789",
                    integrantes = listOf(
                        "Carlos",
                        "Ana"
                    ),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(5, 6)
                ),

                ParticipanteActividadAdmin(
                    id = 3,
                    nombreFamilia = "Familia Rodríguez",
                    correo = "rodriguez@email.com",
                    telefono = "81 3456 7890",
                    integrantes = listOf(
                        "María",
                        "Sofía",
                        "Diego"
                    ),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(7, 8, 9)
                ),

                ParticipanteActividadAdmin(
                    id = 4,
                    nombreFamilia = "Familia López",
                    correo = "lopez@email.com",
                    telefono = "81 4567 8901",
                    integrantes = listOf(
                        "Jorge",
                        "Valeria"
                    ),
                    estado = EstadoParticipante.LISTA_ESPERA
                ),

                ParticipanteActividadAdmin(
                    id = 5,
                    nombreFamilia = "Familia Hernández",
                    correo = "hernandez@email.com",
                    telefono = "81 5678 9012",
                    integrantes = listOf(
                        "Daniel",
                        "Camila"
                    ),
                    estado = EstadoParticipante.LISTA_ESPERA
                )
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Publico.route
    ) {

        composable(Routes.Publico.route) {
            PublicoScreen(
                onIniciarSesionClick = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }

        // ==========================================
        // PANTALLA DE PROTOTIPO (SELECCIÓN DE ROL)
        // ==========================================
//        composable(Routes.SelectorRol.route) {
//            SelectorRolScreen(
//                onFamiliaClick = {
//                    navController.navigate(Routes.Inicio.route) {
//                        popUpTo(Routes.SelectorRol.route) { inclusive = true }
//                    }
//                },
//                onAsociacionClick = {
//                    navController.navigate(Routes.DashboardAdmin.route) {
//                        popUpTo(Routes.SelectorRol.route) { inclusive = true }
//                    }
//                }
//            )
//        }

        // ==========================================
        // RUTAS DE USUARIO FAMILIA
        // ==========================================
        composable(Routes.Inicio.route) {
            InicioScreen(
                nombreUsuario = nombreUsuario.ifEmpty { "Usuario" },

                onExplorarClick = {
                    navController.navigate(Routes.Explorar.route)
                },

                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },

                onMensajesClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Mensajes.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Mensajes.route)
                    }
                },

                mostrarMensajes = familyViewModel.usuario.value != null,

                onPerfilClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Perfil.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Perfil.route)
                    }
                },

                onCampaniaClick = {
                    navController.navigate(Routes.DetalleCampania.route)
                },

                campaigns = campaignState.campaigns,
                estadoActividades = estadoActividades
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { token, usuario ->
                    familyViewModel.guardarSesion(token.access_token)

                    familyViewModel.guardarUsuarioAutenticado(
                        nombre = usuario.full_name,
                        correo = usuario.email,
                        telefono = usuario.phone
                    )

                    rolUsuario = usuario.role.trim().lowercase()
                    nombreUsuario = usuario.full_name

                    android.util.Log.d(
                        "LOGIN_ROLE",
                        "Rol recibido del backend: '${usuario.role}'"
                    )

                    val destino = when (usuario.role.trim().lowercase()) {
                        "admin",
                        "administrador",
                        "association",
                        "asociacion",
                        "organizacion" -> Routes.DashboardAdmin.route

                        else -> Routes.Inicio.route
                    }

                    navController.navigate(destino) {
                        popUpTo(Routes.Publico.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                onRegistroClick = {
                    navController.navigate(Routes.Registro.route)
                }
            )
        }

        composable(Routes.Registro.route) {
            BackHandler {
                navController.navigate(Routes.Publico.route) {
                    popUpTo(Routes.Registro.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            RegistroScreen(
                viewModel = familyViewModel,

                onBackClick = {
                    navController.navigate(Routes.Publico.route) {
                        popUpTo(Routes.Registro.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },

                onContinuar = { nombre, registrarOtros ->
                    nombreUsuario = nombre
                    rolUsuario = "familia"

                    if (registrarOtros) {
                        navController.navigate(Routes.Integrantes.route)
                    } else {
                        navController.navigate(rutaDespuesDeRegistro) {
                            popUpTo(Routes.Registro.route) {
                                inclusive = true
                            }
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
                        popUpTo(Routes.Integrantes.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.Explorar.route) {
            LaunchedEffect(Unit) {
                actividadesViewModel.cargarActividades()
            }
            
            ExplorarScreen(
                estadoActividades = estadoActividades,
                campaigns = campaignState.campaigns,
                cargandoActividades = actividadesViewModel.cargando,
                errorActividades = actividadesViewModel.error,

                onReintentarActividades = {
                    actividadesViewModel.cargarActividades()
                },

                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) {
                            inclusive = true
                        }
                    }
                },

                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },

                onMensajesClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Mensajes.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Mensajes.route)
                    }
                },

                onCampaniaClick = { campaniaId ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("campaniaId", campaniaId)

                    navController.navigate(Routes.DetalleCampania.route)
                },

                onActividadClick = { eventoId ->
                    actividadesViewModel.seleccionarEvento(eventoId)
                    navController.navigate(Routes.DetalleActividad.route)
                },

                onPerfilClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Perfil.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Perfil.route)
                    }
                },

                mostrarMensajes = familyViewModel.usuario.value != null
            )
        }

        composable(Routes.DetalleCampania.route) {
            val campaniaId = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("campaniaId")
                ?: "utiles_escolares"

            DetalleCampaniaScreen(
                campaniaId = campaniaId,

                onBackClick = {
                    navController.popBackStack()
                },

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
                        popUpTo(Routes.Explorar.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.DetalleActividad.route) {
            val entradaActual by navController.currentBackStackEntryAsState()

            LaunchedEffect(
                entradaActual,
                familyViewModel.familiaId,
                actividadSeleccionada.id
            ) {
                if (
                    entradaActual?.destination?.route == Routes.DetalleActividad.route &&
                    familyViewModel.familiaId.isNotBlank()
                ) {
                    actividadesViewModel.cargarMisInscripcionesYEncuestas(
                        familyViewModel.familiaId
                    )
                }
            }

            DetalleActividadScreen(
                inscripcionConfirmada = estadoActividades.inscripciones.any {
                    it.eventoId == actividadSeleccionada.id &&
                            it.familiaId == familyViewModel.familiaId &&
                            it.estado == mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
                },

                error = actividadesViewModel.error,

                onCancelarInscripcionClick = { onResult ->
                    actividadesViewModel.cancelarInscripcion(
                        familyViewModel.familiaId,
                        onResult
                    )
                },

                actividad = actividadSeleccionada,
                ocupados = estadoActividades.ocupados(actividadSeleccionada.id),
                disponibles = estadoActividades.disponibles(actividadSeleccionada.id),

                inscripcionesCerradas =
                    System.currentTimeMillis() >= actividadSeleccionada.cierreInscripciones,

                inscripcionActiva = estadoActividades.inscripciones.any {
                    it.eventoId == actividadSeleccionada.id &&
                            it.familiaId == familyViewModel.familiaId &&
                            it.estado in listOf(
                        mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA,
                        mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA
                    )
                },

                onBackClick = {
                    navController.popBackStack()
                },

                onConsultarCalendarioClick = {
                    navController.navigate(Routes.Actividades.route) {
                        launchSingleTop = true
                    }
                },

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
                actividad = actividadSeleccionada,

                onBackClick = {
                    navController.popBackStack()
                },

                enEspera = estadoActividades.disponibles(actividadSeleccionada.id) == 0,
                error = actividadesViewModel.error,

                onConfirmarClick = { participantes, observaciones ->

                    actividadesViewModel.inscribir(
                        familyViewModel.familiaId,
                        participantes,
                        observaciones
                    ) { exito, estadoRemoto ->

                        if (!exito) {
                            if (!estadoRemoto.isNullOrBlank()) {
                                Toast.makeText(
                                    context,
                                    "Tu familia ya tiene una inscripción activa en esta actividad.",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack(
                                    Routes.DetalleActividad.route,
                                    inclusive = false
                                )
                            }

                            return@inscribir
                        }

                        if (estadoRemoto.isNullOrBlank()) {
                            return@inscribir
                        }

                        val estadoNormalizado = estadoRemoto
                            .trim()
                            .uppercase(java.util.Locale.ROOT)

                        val confirmadaRemota =
                            estadoNormalizado == "CONFIRMADA"

                        val enEsperaRemota = estadoNormalizado in setOf(
                            "EN_ESPERA",
                            "LISTA_ESPERA"
                        )

                        if (!confirmadaRemota && !enEsperaRemota) {
                            Toast.makeText(
                                context,
                                "El servidor devolvió un estado de inscripción inesperado.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@inscribir
                        }

                        val mensaje = if (confirmadaRemota) {
                            "¡Inscripción confirmada!"
                        } else {
                            "Tu familia quedó en lista de espera."
                        }

                        Toast.makeText(
                            context,
                            mensaje,
                            Toast.LENGTH_LONG
                        ).show()

                        navController.popBackStack(
                            Routes.DetalleActividad.route,
                            inclusive = false
                        )
                    }
                }
            )
        }

        composable(Routes.ConfirmacionInscripcion.route) {
            val solicitud = estadoActividades.inscripciones.firstOrNull {
                it.id == actividadesViewModel.ultimaInscripcionId
            }

            val actividad =
                solicitud?.let {
                    estadoActividades.evento(it.eventoId)
                } ?: actividadSeleccionada

            val estadoNormalizado = actividadesViewModel
                .ultimaInscripcionEstado
                ?.trim()
                ?.uppercase(java.util.Locale.ROOT)

            val estadoInscripcion = when (estadoNormalizado) {
                "CONFIRMADA" ->
                    mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA

                "EN_ESPERA", "LISTA_ESPERA" ->
                    mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA

                else ->
                    solicitud?.estado
                        ?: mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
            }

            val posicion = solicitud?.let {
                if (
                    it.estado ==
                    mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA
                ) {
                    estadoActividades.posicion(it)
                } else {
                    null
                }
            }

            ConfirmacionInscripcionScreen(
                actividad = actividad,
                estado = estadoInscripcion,
                posicion = posicion,

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
            var mostrarMisActividades by remember {
                mutableStateOf(false)
            }

            if (mostrarMisActividades) {
                MisActividadesScreen(
                    actividadesViewModel = actividadesViewModel,
                    familiaId = familyViewModel.familiaId,

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

                    mostrarMensajes =
                        familyViewModel.usuario.value != null
                )
            } else {
                CalendarioScreen(
                    estadoActividades = estadoActividades,
                    familiaId = familyViewModel.familiaId,

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

                    onActividadClick = { actividad ->
                        actividadesViewModel.seleccionarEvento(actividad.id)
                        navController.navigate(Routes.DetalleActividad.route)
                    },

                    onMisActividadesClick = {
                        mostrarMisActividades = true
                    },

                    mostrarMensajes =
                        familyViewModel.usuario.value != null
                )
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
            val conversacion = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get("conversacion") as? Conversacion

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
                },

                onCambiarRolClick = {
                    familyViewModel.usuario.value = null

                    navController.navigate(Routes.Publico.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },

                onCancelarInscripcionClick = {
                    familyViewModel.usuario.value = null

                    navController.navigate(Routes.Registro.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ==========================================
        // RUTAS DE ADMINISTRADOR (ASOCIACIÓN)
        // ==========================================
        composable(Routes.DashboardAdmin.route) {
            val rolesAdmin = setOf(
                "admin",
                "administrador",
                "association",
                "asociacion",
                "organizacion"
            )

            val tieneAccesoAdmin =
                !familyViewModel.accessToken.value.isNullOrBlank() &&
                        rolUsuario in rolesAdmin
            LaunchedEffect(tieneAccesoAdmin) {
                if (tieneAccesoAdmin) {
                    adminPublicationsViewModel.loadActivities()
                }
            }

            if (!tieneAccesoAdmin) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.Publico.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            } else {
                val managedActivities = adminPublicationsState.activities.filter {
                    it.kind == PublicationKind.ACTIVITY
                }
                val primaryActivity = managedActivities.firstOrNull()
                DashboardAdminScreen(
                    tituloActividad = primaryActivity?.title
                        ?: "Sin actividades publicadas",
                    participantesActividad = primaryActivity?.confirmedPeople ?: 0,
                    cuposTotalesActividad =
                        primaryActivity?.capacityPeople?.toIntOrNull() ?: 0,
                    fechaActividad = primaryActivity?.startsAt?.take(16).orEmpty(),
                    actividadId = primaryActivity?.id,
                    actividadStatus = primaryActivity?.status.orEmpty(),
                    managedActivities = managedActivities,
                    actividadSecundaria = managedActivities.getOrNull(1),
                    isLoadingActivities = adminPublicationsState.isLoading,
                    activityError = adminPublicationsState.error,

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

                    onPerfilClick = {
                        navController.navigate(Routes.ConfiguracionAdmin.route)
                    },

                    onCrearCampaniaClick = {
                        navController.navigate(
                            Routes.CrearCampania.route
                        )
                    },

                    onActividadClick = { activityId ->
                        navController.navigate(
                            Routes.DetalleActividadAdmin.createRoute(activityId)
                        )
                    }
                )
            }
        }

        composable(Routes.MisCampanias.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val campaignViewModel = remember { CampaignViewModel(sessionManager) }
                val uiState by campaignViewModel.uiState.collectAsState()

                MisCampaniasScreen(
                    campaigns = uiState.campaigns,
                    isLoading = uiState.isLoading,
                    error = uiState.error,

                    onInicioClick = {
                        navController.navigate(Routes.DashboardAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route) {
                                inclusive = true
                            }
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

                    onNuevaCampaniaClick = {
                        navController.navigate(
                            Routes.CrearCampania.route
                        )
                    },

                    onGestionarPublicacionesClick = {
                        navController.navigate(Routes.GestionPublicaciones.route)
                    },

                    onGestionCampaniaClick = {
                        navController.navigate(
                            Routes.GestionMensajesCampania.route
                        )
                    },

                    onEditarCampaniaClick = { campaignId ->
                        navController.navigate(
                            Routes.EditarCampania.createRoute(campaignId)
                        )
                    }
                )
            }
        }

        composable(Routes.EditarCampania.route + "/{campaignId}") { backStackEntry ->
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                LaunchedEffect(Unit) {
                    adminPublicationsViewModel.loadActivities()
                }
                val campaignIdStr = backStackEntry.arguments?.getString("campaignId") ?: "0"
                val campaignId = campaignIdStr.toIntOrNull() ?: 0
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val campaignViewModel = remember { CampaignViewModel(sessionManager) }
                val uiState by campaignViewModel.uiState.collectAsState()

                EditarCampaniaScreen(
                    campaignId = campaignId,
                    campaigns = uiState.campaigns,
                    activities = adminPublicationsState.activities,
                    canChangeStatus = rolUsuario == "admin",
                    isSaving = uiState.isLoading,
                    isSavingActivity = adminPublicationsState.isLoading,
                    error = uiState.error,
                    activityError = adminPublicationsState.error,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSave = { id, request, photoUri ->
                        campaignViewModel.updateCampaign(
                            context,
                            id,
                            request,
                            photoUri
                        ) {
                            campaignViewModel.loadCampaigns()
                        }
                    },
                    onSaveActivity = { activity, photoUri ->
                        adminPublicationsViewModel.updatePublication(
                            activity,
                            onSuccess = {},
                            photoContext = context,
                            photoUri = photoUri
                        )
                    }
                )
            }
        }

        composable(Routes.GestionPublicaciones.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                LaunchedEffect(Unit) {
                    adminPublicationsViewModel.loadPublications()
                }
                GestionPublicacionesScreen(
                    state = adminPublicationsState,
                    onBackClick = { navController.popBackStack() },
                    onRetry = adminPublicationsViewModel::loadPublications,
                    onEditClick = { publication ->
                        if (publication.kind == PublicationKind.CAMPAIGN) {
                            navController.navigate(
                                Routes.EditarCampania.createRoute(publication.id)
                            )
                        } else {
                            navController.navigate(
                                Routes.EditarPublicacion.createRoute(
                                    publication.kind.name.lowercase(),
                                    publication.id
                                )
                            )
                        }
                    }
                )
            }
        }

        composable(Routes.EditarPublicacion.route + "/{kind}/{publicationId}") { backStackEntry ->
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val kindName = backStackEntry.arguments?.getString("kind")
                val publicationId = backStackEntry.arguments
                    ?.getString("publicationId")
                    ?.toIntOrNull()
                val kind = PublicationKind.values()
                    .firstOrNull { it.name.equals(kindName, ignoreCase = true) }
                val publication = if (kind != null && publicationId != null) {
                    adminPublicationsState.find(kind, publicationId)
                } else {
                    null
                }
                val context = LocalContext.current

                EditarPublicacionScreen(
                    publication = publication,
                    isSaving = adminPublicationsState.isLoading,
                    error = adminPublicationsState.error,
                    onBackClick = { navController.popBackStack() },
                    onSave = { draft, photoUri ->
                        adminPublicationsViewModel.updatePublication(
                            draft,
                            photoContext = context,
                            photoUri = photoUri
                        ) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        composable(Routes.DetalleActividadAdmin.route + "/{activityId}") { backStackEntry ->
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val activityId = backStackEntry.arguments
                    ?.getString("activityId")
                    ?.toIntOrNull()
                LaunchedEffect(activityId) {
                    if (activityId != null &&
                        adminPublicationsState.find(PublicationKind.ACTIVITY, activityId) == null
                    ) {
                        adminPublicationsViewModel.loadActivities()
                    }
                }
                val publication = activityId?.let {
                    adminPublicationsState.find(PublicationKind.ACTIVITY, it)
                }

                DetalleActividadAdminScreen(
                    publication = publication,
                    isLoading = adminPublicationsState.isLoading,
                    isCancelling = adminPublicationsState.isLoading,
                    error = adminPublicationsState.error,
                    onBack = { navController.popBackStack() },
                    onEdit = {
                        activityId?.let {
                            navController.navigate(
                                Routes.EditarPublicacion.createRoute(
                                    PublicationKind.ACTIVITY.name.lowercase(),
                                    it
                                )
                            )
                        }
                    },
                    onManageParticipants = {
                        activityId?.let {
                            navController.navigate(
                                Routes.GestionarParticipantesAdmin.createRoute(it)
                            )
                        }
                    },
                    onCancelActivity = {
                        activityId?.let { id ->
                            adminPublicationsViewModel.cancelActivity(id) {
                                navController.popBackStack()
                            }
                        }
                    },
                    onShare = {
                        publication?.let {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    buildString {
                                        append(it.title)
                                        if (it.description.isNotBlank()) {
                                            append("\n")
                                            append(it.description)
                                        }
                                    }
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Compartir actividad")
                            )
                        }
                    }
                )
            }
        }

        composable(Routes.GestionarParticipantesAdmin.route + "/{activityId}") { backStackEntry ->
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val activityId = backStackEntry.arguments
                    ?.getString("activityId")
                    ?.toIntOrNull()
                LaunchedEffect(activityId) {
                    if (activityId != null) {
                        adminPublicationsViewModel.loadActivityRegistrations(activityId)
                    }
                }
                val activityCapacity = activityId
                    ?.let { adminPublicationsState.find(PublicationKind.ACTIVITY, it) }
                    ?.capacityPeople
                    ?.toIntOrNull()
                    ?: 0
                GestionarParticipantesScreen(
                    registrations = adminPublicationsState.activityRegistrations,
                    capacityPeople = activityCapacity,
                    isLoading = adminPublicationsState.isLoadingRegistrations,
                    error = adminPublicationsState.activityManagementError,
                    onBackClick = { navController.popBackStack() },
                    onCancelRegistration = { registrationId ->
                        activityId?.let {
                            adminPublicationsViewModel.cancelActivityRegistration(
                                it,
                                registrationId
                            )
                        }
                    },
                    onAcceptRegistration = { registrationId ->
                        activityId?.let {
                            adminPublicationsViewModel.acceptActivityRegistration(
                                it,
                                registrationId
                            )
                        }
                    }
                )
            }
        }

        composable(Routes.CrearCampania.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val campaignViewModel = remember { CampaignViewModel(sessionManager) }
                val uiState by campaignViewModel.uiState.collectAsState()

                CrearCampaniaScreen(
                    isSaving = uiState.isLoading,
                    error = uiState.error,
                    onBackClick = { navController.popBackStack() },
                    onCreate = { title, description, organizationName, photoUri ->
                        campaignViewModel.createCampaign(
                            context,
                            title,
                            description,
                            organizationName,
                            photoUri
                        ) {
                            navController.navigate(Routes.MisCampanias.route) {
                                popUpTo(Routes.MisCampanias.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }
        }

        composable(Routes.MensajesAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                MensajesAdminScreen(
                    participantes = participantesAdmin,

                    onInicioClick = {
                        navController.navigate(Routes.DashboardAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route) {
                                inclusive = true
                            }
                        }
                    },

                    onCampaniasClick = {
                        navController.navigate(Routes.MisCampanias.route) {
                            popUpTo(Routes.DashboardAdmin.route)
                        }
                    },

                    onChatClick = { userId, tituloChat, imagenId ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("userId", userId)

                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("nombreChat", tituloChat)

                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("imagenId", imagenId)

                        navController.navigate(Routes.ChatAdmin.route)
                    },

                    onConfiguracionClick = {
                        navController.navigate(Routes.ConfiguracionAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route)
                        }
                    },

                    onPerfilClick = {
                        navController.navigate(
                            Routes.ConfiguracionAdmin.route
                        )
                    }
                )
            }
        }

        composable(Routes.ChatAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                val userId = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("userId") ?: 0

                val nombreChat = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("nombreChat") ?: "Chat"

                val imagenId = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Int>("imagenId") ?: R.drawable.icon

                ChatAdminScreen(
                    userId = userId,
                    nombreChat = nombreChat,
                    imagenId = imagenId,

                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.ConfiguracionAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                ConfiguracionAdminScreen(
                    onInicioClick = {
                        navController.navigate(Routes.DashboardAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route) {
                                inclusive = true
                            }
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

                    onCerrarSesionClick = {
                        familyViewModel.cerrarSesion()
                        rolUsuario = null
                        nombreUsuario = ""

                        navController.navigate(Routes.Publico.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(Routes.GestionMensajesCampania.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                GestionMensajesCampaniaScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onInicioClick = {
                        navController.navigate(Routes.DashboardAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route) {
                                inclusive = true
                            }
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
}

@Composable
private fun RutaAdminProtegida(
    tieneAcceso: Boolean,
    navController: NavHostController,
    contenido: @Composable () -> Unit
) {
    if (!tieneAcceso) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.Publico.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    } else {
        contenido()
    }
}
