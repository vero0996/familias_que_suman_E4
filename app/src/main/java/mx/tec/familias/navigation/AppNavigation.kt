package mx.tec.familias.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import mx.tec.familias.R
import mx.tec.familias.data.model.ActividadAdmin
import mx.tec.familias.data.model.EstadoParticipante
import mx.tec.familias.data.model.Evento
import mx.tec.familias.data.model.ParticipanteActividadAdmin
import mx.tec.familias.data.model.Taller
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.ui.auth.ForgotPasswordScreen
import mx.tec.familias.ui.auth.IntegrantesScreen
import mx.tec.familias.ui.auth.LoginScreen
import mx.tec.familias.ui.auth.RegistroScreen
import mx.tec.familias.ui.screens.actividades.CalendarioScreen
import mx.tec.familias.ui.screens.actividades.InscripcionScreen
import mx.tec.familias.ui.screens.actividades.MisActividadesScreen
import mx.tec.familias.ui.screens.admin.campanias.*
import mx.tec.familias.ui.screens.admin.campanias.DetalleActividadScreen as DetalleActividadAdminScreen
import mx.tec.familias.ui.screens.admin.configuracion.ConfiguracionAdminScreen
import mx.tec.familias.ui.screens.admin.dashboard.DashboardAdminScreen
import mx.tec.familias.ui.screens.admin.mensajes.ChatAdminScreen
import mx.tec.familias.ui.screens.admin.mensajes.MensajesAdminScreen
import mx.tec.familias.ui.screens.confirmacion.ConfirmacionCampaniaScreen
import mx.tec.familias.ui.screens.confirmacion.ConfirmacionInscripcionScreen
import mx.tec.familias.ui.screens.explorar.DetalleActividadScreen
import mx.tec.familias.ui.screens.explorar.DetalleCampaniaScreen
import mx.tec.familias.ui.screens.explorar.ExplorarScreen
import mx.tec.familias.ui.screens.inicio.InicioScreen
import mx.tec.familias.ui.screens.mensajes.ChatMessages
import mx.tec.familias.ui.screens.mensajes.Conversacion
import mx.tec.familias.ui.screens.mensajes.MensajesScreen
import mx.tec.familias.ui.screens.perfil.PerfilScreen
import mx.tec.familias.ui.screens.publico.PublicoScreen
import mx.tec.familias.viewmodel.ActividadesViewModelFactory
import mx.tec.familias.viewmodel.FamilyViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val familyViewModel: FamilyViewModel = viewModel()
    val campaignViewModel: mx.tec.familias.viewmodel.CampaignViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val actividadesViewModel: mx.tec.familias.viewmodel.ActividadesViewModel =
        viewModel(factory = remember(sessionManager) { ActividadesViewModelFactory(sessionManager) })

    val estadoActividades = actividadesViewModel.estado
    val campaignState by campaignViewModel.uiState.collectAsState()
    val actividadSeleccionada = estadoActividades.evento(actividadesViewModel.eventoSeleccionadoId)

    var nombreUsuario by remember { mutableStateOf("") }
    var rutaDespuesDeRegistro by remember { mutableStateOf(Routes.Inicio.route) }
    var rutaDespuesDeIntegrantes by remember { mutableStateOf(Routes.Inicio.route) }
    var rolUsuario by remember { mutableStateOf<String?>(null) }

    val rolesAdmin = setOf("admin", "administrador", "association", "asociacion", "organizacion")
    val tieneAccesoAdmin = !familyViewModel.accessToken.value.isNullOrBlank() && rolUsuario in rolesAdmin

    var participantesAdmin by remember {
        mutableStateOf(
            listOf(
                ParticipanteActividadAdmin(
                    id = 1,
                    nombreFamilia = "Familia González",
                    correo = "gonzalez@email.com",
                    telefono = "81 1234 5678",
                    integrantes = listOf("Alberto", "Elena", "Lucía", "Mateo"),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(1, 2, 3, 4)
                ),
                ParticipanteActividadAdmin(
                    id = 2,
                    nombreFamilia = "Familia Martínez",
                    correo = "martinez@email.com",
                    telefono = "81 2345 6789",
                    integrantes = listOf("Carlos", "Ana"),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(5, 6)
                ),
                ParticipanteActividadAdmin(
                    id = 3,
                    nombreFamilia = "Familia Rodríguez",
                    correo = "rodriguez@email.com",
                    telefono = "81 3456 7890",
                    integrantes = listOf("María", "Sofía", "Diego"),
                    estado = EstadoParticipante.CONFIRMADO,
                    lugaresAsignados = listOf(7, 8, 9)
                ),
                ParticipanteActividadAdmin(
                    id = 4,
                    nombreFamilia = "Familia López",
                    correo = "lopez@email.com",
                    telefono = "81 4567 8901",
                    integrantes = listOf("Jorge", "Valeria"),
                    estado = EstadoParticipante.LISTA_ESPERA
                ),
                ParticipanteActividadAdmin(
                    id = 5,
                    nombreFamilia = "Familia Hernández",
                    correo = "hernandez@email.com",
                    telefono = "81 5678 9012",
                    integrantes = listOf("Daniel", "Camila"),
                    estado = EstadoParticipante.LISTA_ESPERA
                )
            )
        )
    }

    val totalPersonasConfirmadas = participantesAdmin
        .filter { it.estado == EstadoParticipante.CONFIRMADO }
        .sumOf { it.integrantes.size }

    var actividadAdmin by remember {
        mutableStateOf(
            ActividadAdmin(
                titulo = "Recogida de Invierno - Centro de Acopio",
                descripcion = "Actividad de apoyo para la recolección y organización de donaciones de invierno.",
                fecha = "Sáb, 15 Nov",
                hora = "10:00 AM",
                ubicacion = "Centro de Acopio",
                participantes = totalPersonasConfirmadas,
                cuposTotales = 20,
                esUrgente = true,
                imagenId = R.drawable.recorridainvierno
            )
        )
    }

    var actividadExiste by remember { mutableStateOf(true) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Routes.Publico.route) {

        composable(Routes.Publico.route) {
            PublicoScreen(onIniciarSesionClick = { navController.navigate(Routes.Login.route) })
        }

        // ==========================================
        // RUTAS DE USUARIO FAMILIA
        // ==========================================

        composable(Routes.Inicio.route) {
            InicioScreen(
                nombreUsuario = nombreUsuario.ifEmpty { "Usuario" },
                onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                onActividadesClick = { navController.navigate(Routes.Actividades.route) },
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
                onCampaniaClick = { navController.navigate(Routes.DetalleCampania.route) },
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

                    android.util.Log.d("LOGIN_ROLE", "Rol recibido del backend: '${usuario.role}'")

                    val destino = when (usuario.role.trim().lowercase()) {
                        "admin", "administrador", "association", "asociacion", "organizacion" ->
                            Routes.DashboardAdmin.route
                        else -> Routes.Inicio.route
                    }

                    navController.navigate(destino) {
                        popUpTo(Routes.Publico.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegistroClick = { navController.navigate(Routes.Registro.route) },
                onForgotPasswordClick = { navController.navigate(Routes.ForgotPassword.route) }
            )
        }

        composable(Routes.ForgotPassword.route) {
            ForgotPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.Registro.route) {
            BackHandler {
                navController.navigate(Routes.Publico.route) {
                    popUpTo(Routes.Registro.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            RegistroScreen(
                viewModel = familyViewModel,
                onBackClick = {
                    navController.navigate(Routes.Publico.route) {
                        popUpTo(Routes.Registro.route) { inclusive = true }
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
            var eventosExplorar by remember { mutableStateOf<List<Evento>>(emptyList()) }
            var talleresExplorar by remember { mutableStateOf<List<Taller>>(emptyList()) }

            LaunchedEffect(Unit) {
                actividadesViewModel.cargarActividades()

                try {
                    val response = ApiClient.apiService.getEventos()
                    if (response.isSuccessful) eventosExplorar = response.body().orEmpty()
                } catch (_: Exception) {
                }

                try {
                    val response = ApiClient.apiService.getTalleres()
                    if (response.isSuccessful) talleresExplorar = response.body().orEmpty()
                } catch (_: Exception) {
                }
            }

            ExplorarScreen(
                estadoActividades = estadoActividades,
                campaigns = campaignState.campaigns,
                eventos = eventosExplorar,
                talleres = talleresExplorar,
                cargandoActividades = actividadesViewModel.cargando,
                errorActividades = actividadesViewModel.error,
                onReintentarActividades = { actividadesViewModel.cargarActividades() },

                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },

                onActividadesClick = { navController.navigate(Routes.Actividades.route) },

                onMensajesClick = {
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Mensajes.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Mensajes.route)
                    }
                },

                onCampaniaClick = { campaniaId ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("campaniaId", campaniaId)
                    navController.navigate(Routes.DetalleCampania.route)
                },

                onActividadClick = { eventoId ->
                    actividadesViewModel.seleccionarEvento(eventoId)
                    navController.navigate(Routes.DetalleActividad.route)
                },

                onEventoClick = { eventId ->
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Explorar.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.currentBackStackEntry?.savedStateHandle?.set("eventId", eventId)
                        navController.navigate(Routes.Mensajes.route)
                    }
                },

                onTallerClick = { workshopId ->
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Explorar.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.currentBackStackEntry?.savedStateHandle?.set("workshopId", workshopId)
                        navController.navigate(Routes.Mensajes.route)
                    }
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
            val campaniaId =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("campaniaId")
                    ?: "utiles_escolares"

            DetalleCampaniaScreen(
                campaniaId = campaniaId,
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
            val entradaActual by navController.currentBackStackEntryAsState()

            LaunchedEffect(entradaActual, familyViewModel.familiaId, actividadSeleccionada.id) {
                if (
                    entradaActual?.destination?.route == Routes.DetalleActividad.route &&
                    familyViewModel.familiaId.isNotBlank()
                ) {
                    actividadesViewModel.cargarMisInscripcionesYEncuestas(familyViewModel.familiaId)
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
                    actividadesViewModel.cancelarInscripcion(familyViewModel.familiaId, onResult)
                },

                actividad = actividadSeleccionada,
                ocupados = estadoActividades.ocupados(actividadSeleccionada.id),
                disponibles = estadoActividades.disponibles(actividadSeleccionada.id),
                inscripcionesCerradas = System.currentTimeMillis() >= actividadSeleccionada.cierreInscripciones,

                inscripcionActiva = estadoActividades.inscripciones.any {
                    it.eventoId == actividadSeleccionada.id &&
                            it.familiaId == familyViewModel.familiaId &&
                            it.estado in listOf(
                        mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA,
                        mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA
                    )
                },

                onBackClick = { navController.popBackStack() },

                onConsultarCalendarioClick = {
                    navController.navigate(Routes.Actividades.route) { launchSingleTop = true }
                },

                onMensajesActividadClick = {
                    val activityId = actividadSeleccionada.id.toIntOrNull()

                    if (activityId != null) {
                        navController.currentBackStackEntry?.savedStateHandle?.set("activityId", activityId)
                        navController.navigate(Routes.Mensajes.route)
                    } else {
                        Toast.makeText(
                            context,
                            "No se pudo abrir el chat de esta actividad.",
                            Toast.LENGTH_SHORT
                        ).show()
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
                onBackClick = { navController.popBackStack() },
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

                        if (estadoRemoto.isNullOrBlank()) return@inscribir

                        val estadoNormalizado =
                            estadoRemoto.trim().uppercase(java.util.Locale.ROOT)

                        val confirmadaRemota = estadoNormalizado == "CONFIRMADA"
                        val enEsperaRemota =
                            estadoNormalizado in setOf("EN_ESPERA", "LISTA_ESPERA")

                        if (!confirmadaRemota && !enEsperaRemota) {
                            Toast.makeText(
                                context,
                                "El servidor devolvió un estado de inscripción inesperado.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@inscribir
                        }

                        val usuarioActual = familyViewModel.usuario.value

                        if (usuarioActual != null) {
                            val integrantesSeleccionados =
                                familyViewModel.integrantes
                                    .filter { it.id in participantes }
                                    .map { it.nombre }

                            val integrantesInscritos =
                                listOf(usuarioActual.nombre) + integrantesSeleccionados

                            val lugaresOcupados =
                                participantesAdmin
                                    .filter { it.estado == EstadoParticipante.CONFIRMADO }
                                    .flatMap { it.lugaresAsignados }
                                    .toSet()

                            val lugaresDisponibles =
                                (1..actividadAdmin.cuposTotales).filterNot { it in lugaresOcupados }

                            val lugaresAsignados =
                                if (confirmadaRemota) {
                                    lugaresDisponibles.take(integrantesInscritos.size)
                                } else {
                                    emptyList()
                                }

                            val nuevoParticipante = ParticipanteActividadAdmin(
                                id = (participantesAdmin.maxOfOrNull { it.id } ?: 0) + 1,
                                nombreFamilia = "Familia ${usuarioActual.nombre}",
                                correo = usuarioActual.correo,
                                telefono = usuarioActual.telefono,
                                integrantes = integrantesInscritos,
                                estado = if (confirmadaRemota) {
                                    EstadoParticipante.CONFIRMADO
                                } else {
                                    EstadoParticipante.LISTA_ESPERA
                                },
                                lugaresAsignados = lugaresAsignados
                            )

                            participantesAdmin = participantesAdmin + nuevoParticipante

                            actividadAdmin = actividadAdmin.copy(
                                participantes = participantesAdmin
                                    .filter { it.estado == EstadoParticipante.CONFIRMADO }
                                    .sumOf { it.integrantes.size }
                            )
                        }

                        val mensaje =
                            if (confirmadaRemota) {
                                "¡Inscripción confirmada!"
                            } else {
                                "Tu familia quedó en lista de espera."
                            }

                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

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
                solicitud?.let { estadoActividades.evento(it.eventoId) }
                    ?: actividadSeleccionada

            val estadoNormalizado =
                actividadesViewModel.ultimaInscripcionEstado
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
                if (it.estado == mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA) {
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
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Actividades.route) {
            var mostrarMisActividades by remember { mutableStateOf(false) }

            if (mostrarMisActividades) {
                MisActividadesScreen(
                    actividadesViewModel = actividadesViewModel,
                    familiaId = familyViewModel.familiaId,

                    onInicioClick = {
                        navController.navigate(Routes.Inicio.route) {
                            popUpTo(Routes.Inicio.route) { inclusive = true }
                        }
                    },

                    onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                    onMensajesClick = { navController.navigate(Routes.Mensajes.route) },
                    onPerfilClick = { navController.navigate(Routes.Perfil.route) },
                    onCalendarioClick = { mostrarMisActividades = false },
                    mostrarMensajes = familyViewModel.usuario.value != null
                )
            } else {
                CalendarioScreen(
                    estadoActividades = estadoActividades,
                    familiaId = familyViewModel.familiaId,

                    onInicioClick = {
                        navController.navigate(Routes.Inicio.route) {
                            popUpTo(Routes.Inicio.route) { inclusive = true }
                        }
                    },

                    onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                    onMensajesClick = { navController.navigate(Routes.Mensajes.route) },
                    onPerfilClick = { navController.navigate(Routes.Perfil.route) },

                    onActividadClick = { actividad ->
                        actividadesViewModel.seleccionarEvento(actividad.id)
                        navController.navigate(Routes.DetalleActividad.route)
                    },

                    onMisActividadesClick = { mostrarMisActividades = true },
                    mostrarMensajes = familyViewModel.usuario.value != null
                )
            }
        }

        composable(Routes.Mensajes.route) {
            val activityId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("activityId")

            val eventId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("eventId")

            val workshopId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("workshopId")

            MensajesScreen(
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },

                onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                onActividadesClick = { navController.navigate(Routes.Actividades.route) },
                onPerfilClick = { navController.navigate(Routes.Perfil.route) },

                onConversacionClick = { conversacion ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("conversacion", conversacion)

                    if (activityId != null) {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("activityId", activityId)
                    }

                    if (eventId != null) {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("eventId", eventId)
                    }

                    if (workshopId != null) {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("workshopId", workshopId)
                    }

                    navController.navigate(Routes.ChatMessages.route)
                }
            )
        }

        composable(Routes.ChatMessages.route) {
            val conversacion =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get("conversacion") as? Conversacion

            val activityId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("activityId")

            val eventId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("eventId")

            val workshopId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Int>("workshopId")

            if (conversacion != null) {
                ChatMessages(
                    conversacion = conversacion,
                    activityId = activityId,
                    eventId = eventId,
                    workshopId = workshopId,
                    onBackClick = { navController.popBackStack() }
                )
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
                onMensajesClick = { navController.navigate(Routes.Mensajes.route) },
                onAgregarIntegrante = { navController.navigate(Routes.Integrantes.route) },

                onCambiarRolClick = {
                    familyViewModel.usuario.value = null

                    navController.navigate(Routes.Publico.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },

                onCancelarInscripcionClick = {
                    familyViewModel.usuario.value = null

                    navController.navigate(Routes.Registro.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // RUTAS DE ADMINISTRADOR
        // ==========================================

        composable(Routes.DashboardAdmin.route) {
            LaunchedEffect(Unit) {
                actividadesViewModel.cargarActividades()
            }

            val rolesAdmin =
                setOf("admin", "administrador", "association", "asociacion", "organizacion")

            val tieneAccesoAdmin =
                !familyViewModel.accessToken.value.isNullOrBlank() &&
                        rolUsuario in rolesAdmin

            if (!tieneAccesoAdmin) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.Publico.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                DashboardAdminScreen(
                    tituloActividad = actividadAdmin.titulo,
                    participantesActividad = actividadAdmin.participantes,
                    cuposTotalesActividad = actividadAdmin.cuposTotales,
                    actividades = estadoActividades.eventos,

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
                        navController.navigate(Routes.CrearCampaniaColaborativa.route)
                    },

                    onReutilizarCampaniaClick = {
                        navController.navigate(Routes.ReutilizarCampania.route)
                    },

                    onActividadClick = { activityId ->
                        actividadesViewModel.seleccionarEvento(activityId)
                        navController.navigate(Routes.DetalleActividadAdmin.route)
                    }
                )
            }
        }

        composable(Routes.MisCampanias.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                MisCampaniasScreen(
                    campaigns = campaignState.campaigns,
                    actividades = estadoActividades.eventos,
                    mostrarActividad = actividadExiste,

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

                    onNuevaCampaniaClick = {
                        navController.navigate(Routes.CrearCampaniaColaborativa.route)
                    },

                    onGestionCampaniaClick = {
                        navController.navigate(Routes.GestionMensajesCampania.route)
                    },

                    onEditarCampaniaClick = {
                        navController.navigate(Routes.CrearCampaniaColaborativa.route)
                    },

                    onReutilizarCampaniaClick = {
                        navController.navigate(Routes.ReutilizarCampania.route)
                    },

                    onActividadClick = { activityId ->
                        actividadesViewModel.seleccionarEvento(activityId)
                        navController.navigate(Routes.DetalleActividadAdmin.route)
                    }
                )
            }
        }

        composable(Routes.DetalleActividadAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                DetalleActividadAdminScreen(
                    titulo = actividadSeleccionada.nombre,
                    fecha = actividadSeleccionada.fecha,
                    hora = actividadSeleccionada.hora,
                    ubicacion = actividadSeleccionada.lugar,
                    participantes = actividadSeleccionada.ocupacionInicial,
                    cuposTotales = actividadSeleccionada.capacidadPersonas,
                    esUrgente = false,
                    imagenId = R.drawable.recorridainvierno,

                    onBackClick = { navController.popBackStack() },

                    onEditarClick = {
                        navController.navigate(Routes.FormularioActividadAdmin.route)
                    },

                    onParticipantesClick = {
                        navController.navigate(Routes.GestionarParticipantesAdmin.route)
                    },

                    onMensajesActividadClick = {
                        val activityId = actividadSeleccionada.id.toIntOrNull()

                        if (activityId != null) {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("activityId", activityId)

                            navController.navigate(Routes.MensajesAdmin.route)
                        } else {
                            Toast.makeText(
                                context,
                                "No se pudo abrir el chat de esta actividad.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    onCompartirClick = {},

                    onEliminarClick = {
                        mostrarDialogoEliminar = true
                    }
                )

                if (mostrarDialogoEliminar) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },

                        title = {
                            Text(text = "¿Eliminar actividad?")
                        },

                        text = {
                            Text(
                                text = "Esta acción eliminará la actividad y dejará de estar disponible para los participantes."
                            )
                        },

                        confirmButton = {
                            TextButton(
                                onClick = {
                                    actividadExiste = false
                                    mostrarDialogoEliminar = false

                                    navController.navigate(Routes.MisCampanias.route) {
                                        popUpTo(Routes.DetalleActividadAdmin.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            ) {
                                Text(text = "Eliminar")
                            }
                        },

                        dismissButton = {
                            TextButton(
                                onClick = {
                                    mostrarDialogoEliminar = false
                                }
                            ) {
                                Text(text = "Cancelar")
                            }
                        }
                    )
                }
            }
        }

        composable(Routes.FormularioActividadAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                FormularioActividadAdminScreen(
                    actividad = actividadAdmin,
                    onBackClick = { navController.popBackStack() },
                    onGuardarClick = { actividadActualizada ->
                        actividadAdmin = actividadActualizada
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.GestionarParticipantesAdmin.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                GestionarParticipantesScreen(
                    participantesIniciales = participantesAdmin,
                    cuposTotales = actividadAdmin.cuposTotales,
                    onBackClick = { navController.popBackStack() },

                    onParticipantesChanged = { nuevosParticipantes ->
                        participantesAdmin = nuevosParticipantes

                        val totalPersonasConfirmadas =
                            nuevosParticipantes
                                .filter { it.estado == EstadoParticipante.CONFIRMADO }
                                .sumOf { it.integrantes.size }

                        actividadAdmin =
                            actividadAdmin.copy(participantes = totalPersonasConfirmadas)
                    }
                )
            }
        }

        composable(Routes.CrearCampaniaColaborativa.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                CrearCampaniaColaborativaScreen(
                    onBackClick = { navController.popBackStack() },
                    onContinuarClick = {
                        navController.navigate(Routes.GestionMensajesCampania.route)
                    }
                )
            }
        }

        composable(Routes.ReutilizarCampania.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                ReutilizarCampaniaScreen(
                    onBackClick = { navController.popBackStack() },
                    onVistaPreviaClick = {
                        navController.navigate(Routes.VistaPreviaCampania.route)
                    }
                )
            }
        }

        composable(Routes.VistaPreviaCampania.route) {
            RutaAdminProtegida(
                tieneAcceso = tieneAccesoAdmin,
                navController = navController
            ) {
                VistaPreviaCampaniaScreen(
                    onBackClick = { navController.popBackStack() },

                    onPublicarClick = {
                        navController.navigate(Routes.MisCampanias.route) {
                            popUpTo(Routes.MisCampanias.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.MensajesAdmin.route) {
            RutaAdminProtegida(tieneAcceso = tieneAccesoAdmin, navController = navController) {
                val activityId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("activityId")
                val eventId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("eventId")
                val workshopId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("workshopId")

                MensajesAdminScreen(
                    participantes = participantesAdmin,
                    activityId = activityId,
                    eventIdInicial = eventId,
                    workshopIdInicial = workshopId,

                    onInicioClick = {
                        navController.navigate(Routes.DashboardAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route) { inclusive = true }
                        }
                    },

                    onCampaniasClick = {
                        navController.navigate(Routes.MisCampanias.route) {
                            popUpTo(Routes.DashboardAdmin.route)
                        }
                    },

                    onChatClick = { userId, tituloChat, imagenId, activityIdChat, eventIdChat, workshopIdChat ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("userId", userId)
                        navController.currentBackStackEntry?.savedStateHandle?.set("nombreChat", tituloChat)
                        navController.currentBackStackEntry?.savedStateHandle?.set("imagenId", imagenId)
                        navController.currentBackStackEntry?.savedStateHandle?.set("activityId", activityIdChat)
                        navController.currentBackStackEntry?.savedStateHandle?.set("eventId", eventIdChat)
                        navController.currentBackStackEntry?.savedStateHandle?.set("workshopId", workshopIdChat)
                        navController.navigate(Routes.ChatAdmin.route)
                    },

                    onConfiguracionClick = {
                        navController.navigate(Routes.ConfiguracionAdmin.route) {
                            popUpTo(Routes.DashboardAdmin.route)
                        }
                    },

                    onPerfilClick = { navController.navigate(Routes.ConfiguracionAdmin.route) }
                )
            }
        }

        composable(Routes.ChatAdmin.route) {
            RutaAdminProtegida(tieneAcceso = tieneAccesoAdmin, navController = navController) {
                val userId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("userId") ?: 0
                val nombreChat = navController.previousBackStackEntry?.savedStateHandle?.get<String>("nombreChat") ?: "Chat"
                val imagenId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("imagenId") ?: R.drawable.icon
                val activityId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("activityId")
                val eventId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("eventId")
                val workshopId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("workshopId")

                ChatAdminScreen(
                    userId = userId,
                    nombreChat = nombreChat,
                    imagenId = imagenId,
                    activityId = activityId,
                    eventId = eventId,
                    workshopId = workshopId,
                    onBackClick = { navController.popBackStack() }
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

                    onCerrarSesionClick = {
                        familyViewModel.cerrarSesion()
                        rolUsuario = null
                        nombreUsuario = ""

                        navController.navigate(Routes.Publico.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
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
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    } else {
        contenido()
    }
}