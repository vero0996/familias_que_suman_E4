package mx.tec.familias.ui.screens.explorar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.printToLog
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.onRoot

class ExplorarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val campaign = CampaignResponse(
        id = 101,
        title = "Reforestación comunitaria",
        description = "Ayuda a recuperar áreas verdes",
        organization_name = "Bosque Vivo",
        status = "published",
        created_at = "2026-10-20T10:00:00",
        updated_at = "2026-10-20T10:00:00"
    )

    private val activity = Activity(
        id = "event-1",
        nombre = "Jornada de limpieza",
        organizacion = "Comunidad Verde",
        descripcion = "Limpieza del parque comunitario",
        fecha = "2026-10-25",
        hora = "10:00",
        lugar = "Parque Central",
        capacidadPersonas = 20,
        cierreInscripciones = 0L
    )

    private fun mostrarPantalla(
        campaigns: List<CampaignResponse> = listOf(campaign),
        activities: List<Activity> = listOf(activity)
    ) {
        composeTestRule.setContent {
            FamiliasQueSumanTheme {
                ExplorarScreen(
                    campaigns = campaigns,
                    estadoActividades = EstadoActividades(
                        eventos = activities,
                        inscripciones = emptyList()
                    )
                )
            }
        }
    }

    @Test
    fun pantallaMuestraBuscadorYFiltros() {
        mostrarPantalla()

        composeTestRule
            .onNodeWithText("Buscar causas, asociaciones...")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Todos").assertIsDisplayed()
        composeTestRule.onNodeWithText("Voluntariado").assertIsDisplayed()
        composeTestRule.onNodeWithText("Donaciones").assertIsDisplayed()
    }

    @Test
    fun busquedaEncuentraCampaniaPorNombre() {
        mostrarPantalla()

        composeTestRule
            .onNodeWithText("Buscar causas, asociaciones...")
            .performTextInput("Reforestación")

        composeTestRule
            .onNodeWithText("Reforestación comunitaria")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Jornada de limpieza")
            .assertCountEquals(0)
    }

    @Test
    fun busquedaEncuentraCampaniaPorOrganizacion() {
        mostrarPantalla()

        composeTestRule
            .onNodeWithText("Buscar causas, asociaciones...")
            .performTextInput("Bosque Vivo")

        composeTestRule
            .onNodeWithText("Reforestación comunitaria")
            .assertIsDisplayed()
    }

    @Test
    fun filtroCampaniasOcultaActividades() {
        mostrarPantalla()

        composeTestRule
            .onAllNodesWithText("Campañas")[0]
            .performClick()

        composeTestRule
            .onNodeWithText("Reforestación comunitaria")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Jornada de limpieza")
            .assertCountEquals(0)
    }




    @Test
    fun filtroEventosOcultaCampanias() {
        mostrarPantalla()

        composeTestRule
            .onNodeWithText("Eventos", substring = false)
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Actividades", substring = false)
            .assertExists()

        composeTestRule
            .onNodeWithText("Jornada de limpieza", substring = false)
            .assertExists()

        composeTestRule
            .onNodeWithText("Reforestación comunitaria", substring = false)
            .assertDoesNotExist()
    }

    @Test
    fun busquedaSinCoincidenciasMuestraEstadoVacio() {
        mostrarPantalla()

        composeTestRule
            .onNodeWithText("Buscar causas, asociaciones...")
            .performTextInput("xyz_sin_resultados_123")

        composeTestRule
            .onNodeWithText("No encontramos resultados")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Reforestación comunitaria")
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText("Jornada de limpieza")
            .assertCountEquals(0)
    }
}