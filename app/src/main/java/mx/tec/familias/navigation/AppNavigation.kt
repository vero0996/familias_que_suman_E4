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
import mx.tec.familias.ui.screens.admin.mensajes.ChatAdminScreen
import mx.tec.familias.R
import mx.tec.familias.ui.screens.publico.PublicoScreen

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


import mx.tec.familias.ui.screens.admin.dashboard.DashboardAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.CrearCampaniaColaborativaScreen
import mx.tec.familias.ui.screens.admin.campanias.ReutilizarCampaniaScreen
import mx.tec.familias.ui.screens.admin.campanias.VistaPreviaCampaniaScreen
import mx.tec.familias.ui.screens.admin.mensajes.MensajesAdminScreen
import mx.tec.familias.ui.screens.admin.configuracion.ConfiguracionAdminScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionMensajesCampaniaScreen
import mx.tec.familias.ui.screens.mensajes.MensajesScreen
import mx.tec.familias.ui.screens.mensajes.ChatMessages
import mx.tec.familias.ui.screens.mensajes.Conversacion
import mx.tec.familias.ui.screens.admin.campanias.DetalleActividadScreen as DetalleActividadAdminScreen

import mx.tec.familias.data.model.ActividadAdmin
import mx.tec.familias.ui.screens.admin.campanias.FormularioActividadAdminScreen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import mx.tec.familias.data.model.EstadoParticipante
import mx.tec.familias.data.model.ParticipanteActividadAdmin
import mx.tec.familias.ui.screens.admin.campanias.GestionarParticipantesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val familyViewModel: FamilyViewModel = viewModel()
    val actividadesViewModel: mx.tec.familias.viewmodel.ActividadesViewModel = viewModel()
    val estadoActividades = actividadesViewModel.estado
    val actividadSeleccionada = estadoActividades.evento(actividadesViewModel.eventoSeleccionadoId)

    var nombreUsuario by remember { mutableStateOf("") }
    var rutaDespuesDeRegistro by remember { mutableStateOf(Routes.Inicio.route) }
    var rutaDespuesDeIntegrantes by remember { mutableStateOf(Routes.Inicio.route) }

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

    val totalPersonasConfirmadas = participantesAdmin
        .filter {
            it.estado == EstadoParticipante.CONFIRMADO
        }
        .sumOf {
            it.integrantes.size
        }

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

    var actividadExiste by remember {
        mutableStateOf(true)
    }
    var mostrarDialogoEliminar by remember {
        mutableStateOf(false)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Publico.route
    ) {

        composable(Routes.Publico.route) {
            PublicoScreen(
                onIniciarSesionClick = {
                    // Este navega a tu pantalla de elegir si es familia o asoc.
                    navController.navigate(Routes.SelectorRol.route) {
                        // popUpTo evita que al darle "atrás" regreses a esta pantalla por error
                        popUpTo(Routes.Publico.route) { inclusive = true }
                    }
                },
                onAdminClick = {
                    // Este te manda directo al dashboard de administración para tu presentación
                    navController.navigate(Routes.DashboardAdmin.route) {
                        popUpTo(Routes.Publico.route) { inclusive = true }
                    }
                }
            )
        }

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
                    // Validar inicio de sesión para Mensajes
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
                    // Esto abre la pantalla de detalles de la campaña al presionar "Unirse como Familia" o "Ver detalles"
                    navController.navigate(Routes.DetalleCampania.route)
                },
            )
        }
        composable(Routes.Registro.route) {
            RegistroScreen(
                viewModel = familyViewModel,
                onBackClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },
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
                estadoActividades = estadoActividades,
                onInicioClick = {
                    navController.navigate(Routes.Inicio.route) {
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },

                onActividadesClick = {
                    navController.navigate(Routes.Actividades.route)
                },

                onMensajesClick = {
                    // Validar inicio de sesión para Mensajes
                    if (familyViewModel.usuario.value == null) {
                        rutaDespuesDeRegistro = Routes.Mensajes.route
                        navController.navigate(Routes.Registro.route)
                    } else {
                        navController.navigate(Routes.Mensajes.route)
                    }
                },

                onCampaniaClick = {
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
                inscripcionConfirmada = estadoActividades.inscripciones.any {
                    it.eventoId == actividadSeleccionada.id &&
                            it.familiaId == familyViewModel.familiaId &&
                            it.estado == mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
                },
                error = actividadesViewModel.error,
                onCancelarInscripcionClick = {
                    actividadesViewModel.cancelarInscripcion(familyViewModel.familiaId)
                },
                actividad = actividadSeleccionada,
                ocupados = estadoActividades.ocupados(actividadSeleccionada.id),
                disponibles = estadoActividades.disponibles(actividadSeleccionada.id),
                inscripcionesCerradas = System.currentTimeMillis() >= actividadSeleccionada.cierreInscripciones,
                inscripcionActiva = estadoActividades.inscripciones.any {
                    it.eventoId == actividadSeleccionada.id && it.familiaId == familyViewModel.familiaId &&
                            it.estado in listOf(mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA,
                        mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA)
                },
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
        actividad = actividadSeleccionada,

        onBackClick = {
            navController.popBackStack()
        },

        enEspera = estadoActividades.disponibles(actividadSeleccionada.id) == 0,
        error = actividadesViewModel.error,

        onConfirmarClick = { participantes, observaciones ->

            // 1. Registrar la inscripción en el sistema de actividades
            if (
                actividadesViewModel.inscribir(
                    familyViewModel.familiaId,
                    participantes,
                    observaciones
                )
            ) {

                // 2. Obtener los datos del usuario actual
                val usuarioActual = familyViewModel.usuario.value

                if (usuarioActual != null) {

                    // 3. Obtener los integrantes seleccionados
                    val integrantesSeleccionados =
                        familyViewModel.integrantes
                            .filter { it.id in participantes }
                            .map { it.nombre }

                    // 4. Verificar si el titular también fue seleccionado
                    val titularSeleccionado =
                        participantes.contains(
                            "${familyViewModel.familiaId}:titular"
                        )

                    // 5. Construir la lista de personas inscritas
                    val integrantesInscritos =
                        (if (titularSeleccionado) {
                            listOf(usuarioActual.nombre)
                        } else {
                            emptyList()
                        }) + integrantesSeleccionados

                    // 6. Personas que intenta registrar esta inscripción
                    val personasInscritas = integrantesInscritos.size

                    // 7. Lugares que ya están ocupados en Admin
                    val lugaresOcupados = participantesAdmin
                        .filter {
                            it.estado == EstadoParticipante.CONFIRMADO
                        }
                        .flatMap {
                            it.lugaresAsignados
                        }
                        .toSet()

                    // 8. Lugares disponibles
                    val lugaresDisponibles = (1..actividadAdmin.cuposTotales)
                        .filterNot {
                            it in lugaresOcupados
                        }

                    // 9. Determinar si caben todas las personas
                    val puedeConfirmarse =
                        lugaresDisponibles.size >= personasInscritas

                    // 10. Asignar lugares disponibles
                    val lugaresAsignados =
                        if (puedeConfirmarse) {
                            lugaresDisponibles
                                .take(personasInscritas)
                        } else {
                            emptyList()
                        }

                    // 11. Crear el participante para Admin
                    val nuevoParticipante = ParticipanteActividadAdmin(
                        id = (participantesAdmin.maxOfOrNull { it.id } ?: 0) + 1,
                        nombreFamilia = "Familia ${usuarioActual.nombre}",
                        correo = usuarioActual.correo,
                        telefono = usuarioActual.telefono,
                        integrantes = integrantesInscritos,
                        estado = if (puedeConfirmarse) {
                            EstadoParticipante.CONFIRMADO
                        } else {
                            EstadoParticipante.LISTA_ESPERA
                        },
                        lugaresAsignados = lugaresAsignados
                    )

                    // 12. Agregarlo a la lista que utiliza Admin
                    participantesAdmin =
                        participantesAdmin + nuevoParticipante

                    // 13. Actualizar el contador de participantes
                    actividadAdmin = actividadAdmin.copy(
                        participantes = participantesAdmin
                            .filter {
                                it.estado == EstadoParticipante.CONFIRMADO
                            }
                            .sumOf {
                                it.integrantes.size
                            }
                    )
                }

                // 14. Mostrar confirmación al usuario
                navController.navigate(
                    Routes.ConfirmacionInscripcion.route
                )
            }
        }
    )
}

        composable(Routes.ConfirmacionInscripcion.route) {
            val solicitud = estadoActividades.inscripciones.firstOrNull {
                it.id == actividadesViewModel.ultimaInscripcionId
            }
            if (solicitud != null) ConfirmacionInscripcionScreen(
                actividad = estadoActividades.evento(solicitud.eventoId),
                estado = solicitud.estado,
                posicion = if (solicitud.estado == mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA)
                    estadoActividades.posicion(solicitud) else null,
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
                        actividadesViewModel = actividadesViewModel,
                        familiaId = familyViewModel.familiaId,
                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) { inclusive = true }
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
                        estadoActividades = estadoActividades,
                        familiaId = familyViewModel.familiaId,
                        onInicioClick = {
                            navController.navigate(Routes.Inicio.route) {
                                popUpTo(Routes.Inicio.route) { inclusive = true }
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
            // CORRECCIÓN: Se fuerza el tipo de dato (as? Conversacion) para que Android Studio no marque error.
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
                        popUpTo(Routes.Inicio.route) { inclusive = true }
                    }
                },
                onExplorarClick = { navController.navigate(Routes.Explorar.route) },
                onActividadesClick = { navController.navigate(Routes.Actividades.route) },
                onAgregarIntegrante = { navController.navigate(Routes.Integrantes.route) },
                onCambiarRolClick = {
                    familyViewModel.usuario.value = null
                    navController.navigate(Routes.SelectorRol.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                // NUEVA LÓGICA DE CANCELACIÓN DE INSCRIPCIÓN:
                onCancelarInscripcionClick = {
                    // 1. Limpiamos la sesión del usuario
                    familyViewModel.usuario.value = null

                    // 2. Lo mandamos a la pantalla de registro y borramos el historial para que no pueda dar "Atrás"
                    navController.navigate(Routes.Registro.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // RUTAS DE ADMINISTRADOR (ASOCIACIÓN)
        // ==========================================
        composable(Routes.DashboardAdmin.route) {
            DashboardAdminScreen(
                tituloActividad = actividadAdmin.titulo,
                participantesActividad = actividadAdmin.participantes,
                cuposTotalesActividad = actividadAdmin.cuposTotales,
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
                onReutilizarCampaniaClick = { navController.navigate(Routes.ReutilizarCampania.route) },
                onActividadClick = {
                    navController.navigate(Routes.DetalleActividadAdmin.route)
                }
            )
        }

        composable(Routes.MisCampanias.route) {
            MisCampaniasScreen(
                mostrarActividad = actividadExiste,

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
                        Routes.CrearCampaniaColaborativa.route
                    )
                },

                onGestionCampaniaClick = {
                    navController.navigate(
                        Routes.GestionMensajesCampania.route
                    )
                },

                onEditarCampaniaClick = {
                    navController.navigate(
                        Routes.CrearCampaniaColaborativa.route
                    )
                },

                onReutilizarCampaniaClick = {
                    navController.navigate(
                        Routes.ReutilizarCampania.route
                    )
                }
            )
        }

        composable(Routes.DetalleActividadAdmin.route) {
            DetalleActividadAdminScreen(
                titulo = actividadAdmin.titulo,
                fecha = actividadAdmin.fecha,
                hora = actividadAdmin.hora,
                ubicacion = actividadAdmin.ubicacion,
                participantes = actividadAdmin.participantes,
                cuposTotales = actividadAdmin.cuposTotales,
                esUrgente = actividadAdmin.esUrgente,
                imagenId = actividadAdmin.imagenId,

                onBackClick = {
                    navController.popBackStack()
                },

                onEditarClick = {
                    navController.navigate(
                        Routes.FormularioActividadAdmin.route
                    )
                },

                onParticipantesClick = {
                    navController.navigate(
                        Routes.GestionarParticipantesAdmin.route
                    )
                },

                onCompartirClick = {
                    // Lo conectaremos después
                },

                onEliminarClick = {
                    mostrarDialogoEliminar = true
                }
            )

            if (mostrarDialogoEliminar) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarDialogoEliminar = false
                    },

                    title = {
                        Text(
                            text = "¿Eliminar actividad?"
                        )
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

                                navController.navigate(
                                    Routes.MisCampanias.route
                                ) {
                                    popUpTo(
                                        Routes.DetalleActividadAdmin.route
                                    ) {
                                        inclusive = true
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Eliminar"
                            )
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarDialogoEliminar = false
                            }
                        ) {
                            Text(
                                text = "Cancelar"
                            )
                        }
                    }
                )
            }
        }

        composable(Routes.FormularioActividadAdmin.route) {
            FormularioActividadAdminScreen(
                actividad = actividadAdmin,

                onBackClick = {
                    navController.popBackStack()
                },

                onGuardarClick = { actividadActualizada ->

                    actividadAdmin = actividadActualizada

                    navController.popBackStack()
                }
            )
        }

        composable(Routes.GestionarParticipantesAdmin.route) {

            GestionarParticipantesScreen(
                participantesIniciales = participantesAdmin,
                cuposTotales = actividadAdmin.cuposTotales,

                onBackClick = {
                    navController.popBackStack()
                },

                onParticipantesChanged = { nuevosParticipantes ->

                    participantesAdmin = nuevosParticipantes

                    val totalPersonasConfirmadas = nuevosParticipantes
                        .filter {
                            it.estado == EstadoParticipante.CONFIRMADO
                        }
                        .sumOf {
                            it.integrantes.size
                        }

                    actividadAdmin = actividadAdmin.copy(
                        participantes = totalPersonasConfirmadas
                    )
                }
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
                    }
                },
                onChatClick = { tituloChat, imagenId ->
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
                onPerfilClick = { navController.navigate(Routes.ConfiguracionAdmin.route) }
            )
        }

        composable(Routes.ChatAdmin.route) {
            val nombreChat = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get("nombreChat") ?: "Chat de Asociación"

            val imagenId = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get("imagenId") ?: R.drawable.icon // <-- Recuperamos la foto

            ChatAdminScreen(
                nombreChat = nombreChat,
                imagenId = imagenId, // <-- Se la inyectamos a la pantalla del chat
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
                onCerrarSesionClick = {
                    navController.navigate(Routes.SelectorRol.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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