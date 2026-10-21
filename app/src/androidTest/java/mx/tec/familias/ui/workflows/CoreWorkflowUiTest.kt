package mx.tec.familias.ui.workflows

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.model.UserProfile
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.ManagedActivityRegistrationResponse
import mx.tec.familias.ui.auth.LoginScreen
import mx.tec.familias.ui.auth.SelectorRolScreen
import mx.tec.familias.ui.screens.actividades.InscripcionScreen
import mx.tec.familias.ui.screens.admin.campanias.CrearCampaniaScreen
import mx.tec.familias.ui.screens.admin.campanias.GestionarParticipantesScreen
import mx.tec.familias.ui.screens.admin.campanias.MisCampaniasScreen
import mx.tec.familias.ui.screens.perfil.PerfilScreen
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme
import mx.tec.familias.viewmodel.FamilyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CoreWorkflowUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectorDeRolInvocaLaOpcionElegida() {
        var familiaSeleccionada = false
        var asociacionSeleccionada = false

        composeRule.setContent {
            FamiliasQueSumanTheme {
                SelectorRolScreen(
                    onFamiliaClick = { familiaSeleccionada = true },
                    onAsociacionClick = { asociacionSeleccionada = true }
                )
            }
        }

        composeRule.onNodeWithText("Entrar como Familia").performClick()
        assertTrue(familiaSeleccionada)
        assertTrue(!asociacionSeleccionada)

        composeRule.onNodeWithText("Entrar como Asociación").performClick()
        assertTrue(asociacionSeleccionada)
    }

    @Test
    fun loginRechazaFormularioVacioSinLlamarAlServidor() {
        composeRule.setContent {
            FamiliasQueSumanTheme {
                LoginScreen(onLoginSuccess = { _, _ ->
                    error("No debe iniciar sesión con campos vacíos")
                })
            }
        }

        composeRule.onNodeWithText("Iniciar sesión").performClick()
        composeRule
            .onNodeWithText("Ingresa tu correo y contraseña.")
            .assertIsDisplayed()
    }

    @Test
    fun loginNavegaARegistroYRecuperacionDeContrasena() {
        var registroSolicitado = false
        var recuperacionSolicitada = false

        composeRule.setContent {
            FamiliasQueSumanTheme {
                LoginScreen(
                    onLoginSuccess = { _, _ -> },
                    onRegistroClick = { registroSolicitado = true },
                    onForgotPasswordClick = { recuperacionSolicitada = true }
                )
            }
        }

        composeRule.onNodeWithText("¿No tienes cuenta? Regístrate").performClick()
        composeRule.onNodeWithText("Olvidé mi contraseña").performClick()

        assertTrue(registroSolicitado)
        assertTrue(recuperacionSolicitada)
    }

    @Test
    fun formularioDeCampaniaValidaCamposYEntregaLosDatos() {
        var submittedTitle: String? = null
        var submittedDescription: String? = null
        var submittedOrganization: String? = null

        composeRule.setContent {
            FamiliasQueSumanTheme {
                CrearCampaniaScreen(
                    onCreate = { title, description, organization, _ ->
                        submittedTitle = title
                        submittedDescription = description
                        submittedOrganization = organization
                    }
                )
            }
        }

        composeRule
            .onNode(hasText("Crear campaña") and hasClickAction())
            .assertIsNotEnabled()
        composeRule.onNodeWithText("Título").performTextInput("Huerto comunitario")
        composeRule.onNodeWithText("Descripción").performTextInput("Sembrar alimentos")
        composeRule
            .onNodeWithText("Nombre de la organización")
            .performTextInput("Manos Unidas")

        composeRule
            .onNode(hasText("Crear campaña") and hasClickAction())
            .performScrollTo()
            .performClick()

        assertEquals("Huerto comunitario", submittedTitle)
        assertEquals("Sembrar alimentos", submittedDescription)
        assertEquals("Manos Unidas", submittedOrganization)
    }

    @Test
    fun inscripcionPideConfirmacionAntesDeEnviarParticipantes() {
        val familyViewModel = FamilyViewModel().apply {
            guardarUsuario(
                nombre = "Familia de prueba",
                correo = "familia@example.test",
                telefono = "5551234567"
            )
        }
        val activity = Activity(
            id = "event-test",
            nombre = "Jornada de voluntariado",
            organizacion = "Manos Unidas",
            descripcion = "Actividad de prueba",
            fecha = "25/10/2026",
            hora = "10:00",
            lugar = "Centro comunitario",
            capacidadPersonas = 10,
            cierreInscripciones = 0L
        )
        var submitted = false

        composeRule.setContent {
            FamiliasQueSumanTheme {
                InscripcionScreen(
                    viewModel = familyViewModel,
                    actividad = activity,
                    onConfirmarClick = { _, _ -> submitted = true }
                )
            }
        }

        composeRule.onNodeWithText("Jornada de voluntariado").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar inscripción").performClick()
        composeRule.onNodeWithText("¿Confirmar inscripción?").assertIsDisplayed()
        assertTrue(!submitted)

        composeRule.onNodeWithText("Sí, confirmar").performClick()
        assertTrue(submitted)
    }

    @Test
    fun perfilPermiteAbrirGestionDeIntegrantesYCerrarSesion() {
        val familyViewModel = FamilyViewModel().apply {
            guardarUsuario(
                nombre = "Familia de prueba",
                correo = "familia@example.test",
                telefono = "5551234567"
            )
        }
        var openFamilyMembers = false
        var logout = false

        composeRule.setContent {
            FamiliasQueSumanTheme {
                PerfilScreen(
                    viewModel = familyViewModel,
                    onInicioClick = {},
                    onExplorarClick = {},
                    onActividadesClick = {},
                    onAgregarIntegrante = { openFamilyMembers = true },
                    onCambiarRolClick = { logout = true }
                )
            }
        }

        composeRule.onNodeWithText("Gestionar integrantes").performScrollTo().performClick()
        composeRule.onNodeWithText("Cerrar sesión").performScrollTo().performClick()

        assertTrue(openFamilyMembers)
        assertTrue(logout)
    }

    @Test
    fun campaniasAdministrativasPermitenEditarLaCampaniaCorrecta() {
        val campaign = CampaignResponse(
            id = 42,
            title = "Campaña de prueba",
            description = "Descripción de prueba",
            organization_name = "Organización de prueba",
            status = "publicada",
            created_at = "2026-10-21T10:00:00",
            updated_at = "2026-10-21T10:00:00"
        )
        var editedCampaignId: Int? = null
        var openedParticipants = false

        composeRule.setContent {
            FamiliasQueSumanTheme {
                MisCampaniasScreen(
                    campaigns = listOf(campaign),
                    onEditarCampaniaClick = { editedCampaignId = it },
                    onGestionCampaniaClick = { openedParticipants = true }
                )
            }
        }

        composeRule.onNodeWithText("Campaña de prueba").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Editar campaña").performClick()
        composeRule.onNodeWithContentDescription("Participantes").performClick()

        assertEquals(42, editedCampaignId)
        assertTrue(openedParticipants)
    }

    @Test
    fun gestionDeParticipantesConfirmaAntesDeAceptarListaDeEspera() {
        val waitingFamily = ManagedActivityRegistrationResponse(
            id = 17,
            activity_id = 3,
            family_id = 9,
            family_name = "Familia en espera",
            contact_name = "Contacto",
            email = "familia@example.test",
            status = "lista_espera",
            participant_names = listOf("Participante"),
            people_count = 1,
            created_at = "2026-10-21T10:00:00"
        )
        var acceptedRegistrationId: Int? = null

        composeRule.setContent {
            FamiliasQueSumanTheme {
                GestionarParticipantesScreen(
                    registrations = listOf(waitingFamily),
                    capacityPeople = 2,
                    isLoading = false,
                    error = null,
                    onBackClick = {},
                    onCancelRegistration = {},
                    onAcceptRegistration = { acceptedRegistrationId = it }
                )
            }
        }

        composeRule.onNodeWithText("Familia en espera").assertIsDisplayed()
        composeRule
            .onNodeWithText("Aceptar familia", substring = true)
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("Aceptar inscripción").assertIsDisplayed()
        assertEquals(null, acceptedRegistrationId)

        composeRule
            .onNode(hasText("Aceptar familia") and hasClickAction())
            .performClick()
        assertEquals(17, acceptedRegistrationId)
    }

    @Test
    fun gestionDeParticipantesPideConfirmacionParaCancelar() {
        val confirmedFamily = ManagedActivityRegistrationResponse(
            id = 18,
            activity_id = 3,
            family_id = 10,
            family_name = "Familia confirmada",
            contact_name = "Contacto",
            email = "familia@example.test",
            status = "confirmada",
            participant_names = listOf("Participante"),
            people_count = 1,
            created_at = "2026-10-21T10:00:00"
        )
        var canceledRegistrationId: Int? = null

        composeRule.setContent {
            FamiliasQueSumanTheme {
                GestionarParticipantesScreen(
                    registrations = listOf(confirmedFamily),
                    capacityPeople = 3,
                    isLoading = false,
                    error = null,
                    onBackClick = {},
                    onCancelRegistration = { canceledRegistrationId = it },
                    onAcceptRegistration = {}
                )
            }
        }

        composeRule
            .onNodeWithText("Cancelar inscripción", substring = true)
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("¿Quieres cancelar la inscripción de Familia confirmada?")
            .assertIsDisplayed()
        assertEquals(null, canceledRegistrationId)

        composeRule
            .onNode(hasText("Cancelar inscripción") and hasClickAction())
            .performClick()
        assertEquals(18, canceledRegistrationId)
    }

    @Test
    fun gestionDeParticipantesNoOfreceAceptarSiNoHayCupoSuficiente() {
        val waitingFamily = ManagedActivityRegistrationResponse(
            id = 19,
            activity_id = 3,
            family_id = 11,
            family_name = "Familia con dos integrantes",
            contact_name = "Contacto",
            email = "familia@example.test",
            status = "lista_espera",
            participant_names = listOf("Integrante 1", "Integrante 2"),
            people_count = 2,
            created_at = "2026-10-21T10:00:00"
        )

        composeRule.setContent {
            FamiliasQueSumanTheme {
                GestionarParticipantesScreen(
                    registrations = listOf(waitingFamily),
                    capacityPeople = 1,
                    isLoading = false,
                    error = null,
                    onBackClick = {},
                    onCancelRegistration = {},
                    onAcceptRegistration = {}
                )
            }
        }

        composeRule.onNodeWithText(
            "Faltan 1 lugares para aceptar a esta familia."
        ).assertIsDisplayed()
        composeRule.onAllNodesWithText("Aceptar familia").assertCountEquals(0)
    }
}
