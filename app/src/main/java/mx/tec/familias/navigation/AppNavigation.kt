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
        startDestination = Routes.SelectorRol.route
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

                onActividadClick = {
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
                viewModel = familyViewModel, // <-- Le pasamos el ViewModel aquí
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

                onBackClick = {
                    navController.popBackStack()
                },

                onConfirmarClick = { integrantesSeleccionados, observaciones ->

                    // 1. Guardar la inscripción real
                    familyViewModel.agregarInscripcion(
                        actividad = "Plantación de Árboles en El Pardo",
                        integrantesSeleccionados = integrantesSeleccionados,
                        observaciones = observaciones
                    )

                    // 2. Recuperar la inscripción que acabamos de guardar
                    val inscripcion = familyViewModel.inscripciones.lastOrNull()

                    if (inscripcion != null) {

                        // 3. Personas que ya tienen lugar en la actividad
                        val personasConfirmadas = participantesAdmin
                            .filter {
                                it.estado == EstadoParticipante.CONFIRMADO
                            }
                            .sumOf {
                                it.integrantes.size
                            }

                        // 4. Personas que intenta registrar esta inscripción
                        val personasInscritas = inscripcion.integrantes.size

                        // 5. Determinar si caben todas las personas
                        val puedeConfirmarse =
                            personasConfirmadas + personasInscritas <= actividadAdmin.cuposTotales

                        // 6. Asignar lugares si hay espacio
                        val lugaresAsignados =
                            if (puedeConfirmarse) {
                                (
                                        personasConfirmadas + 1 ..
                                                personasConfirmadas + personasInscritas
                                        ).toList()
                            } else {
                                emptyList()
                            }

                        // 7. Crear el participante para Admin
                        val nuevoParticipante = ParticipanteActividadAdmin(
                            id = (participantesAdmin.maxOfOrNull { it.id } ?: 0) + 1,
                            nombreFamilia = "Familia ${inscripcion.nombreUsuario}",
                            correo = inscripcion.correo,
                            telefono = inscripcion.telefono,
                            integrantes = inscripcion.integrantes,
                            estado = if (puedeConfirmarse) {
                                EstadoParticipante.CONFIRMADO
                            } else {
                                EstadoParticipante.LISTA_ESPERA
                            },
                            lugaresAsignados = lugaresAsignados
                        )

                        // 8. Agregarlo a la lista que usa Admin
                        participantesAdmin = participantesAdmin + nuevoParticipante

                        // 9. Actualizar el contador de la actividad
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

                    // 10. Mostrar confirmación al usuario
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
                        onActividadClick = {
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