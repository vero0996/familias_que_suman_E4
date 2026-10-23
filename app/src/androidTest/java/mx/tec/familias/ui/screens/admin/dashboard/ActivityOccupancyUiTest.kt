package mx.tec.familias.ui.screens.admin.dashboard

import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import mx.tec.familias.data.model.Activity
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme
import mx.tec.familias.viewmodel.EditablePublication
import mx.tec.familias.viewmodel.PublicationKind
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ActivityOccupancyUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun dashboardShowsBackendOccupancyAndAvailablePlaces() {
        val secondActivity = EditablePublication(
            kind = PublicationKind.ACTIVITY,
            id = 2,
            title = "Actividad secundaria real",
            description = "Prueba",
            organizationName = "Organización",
            startsAt = "21/10/2026 10:00",
            location = "Monterrey",
            capacityPeople = "6",
            confirmedPeople = 2
        )

        composeRule.setContent {
            FamiliasQueSumanTheme {
                DashboardAdminScreen(
                    tituloActividad = "Actividad principal real",
                    participantesActividad = 4,
                    cuposTotalesActividad = 10,
                    fechaActividad = "20/10/2026",
                    horaActividad = "09:00",
                    actividadSecundaria = secondActivity,
                    managedActivities = listOf(secondActivity)
                )
            }
        }

        assertTrue(
            composeRule.onAllNodesWithText(
                "4 personas confirmadas de 10 lugares"
            ).fetchSemanticsNodes().isNotEmpty()
        )
        assertTrue(
            composeRule.onAllNodesWithText("6 disponibles")
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
        assertTrue(
            composeRule.onAllNodesWithText("Actividad secundaria real")
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
        assertTrue(
            composeRule.onAllNodesWithText("2 participantes")
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
    }
}
