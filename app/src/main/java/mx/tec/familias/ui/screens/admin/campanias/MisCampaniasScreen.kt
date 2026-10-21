package mx.tec.familias.ui.screens.admin.campanias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.ui.components.AdminBottomNavigationBar
import mx.tec.familias.ui.components.AdminDestination
import mx.tec.familias.ui.components.AdminScreenHeader
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextSecondary
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme

@Composable
fun MisCampaniasScreen(
    campaigns: List<CampaignResponse> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onInicioClick: () -> Unit = {},
    onMensajesClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onNuevaCampaniaClick: () -> Unit = {},
    onGestionarPublicacionesClick: () -> Unit = {},
    onGestionCampaniaClick: () -> Unit = {},
    onEditarCampaniaClick: (Int) -> Unit = {}
) {
    Scaffold(
        containerColor = TealLight.copy(alpha = 0.12f),
        topBar = { AdminScreenHeader("Mis campañas") },
        bottomBar = {
            AdminBottomNavigationBar(
                currentDestination = AdminDestination.CAMPANIAS,
                onDestinationSelected = { destination ->
                    when (destination) {
                        AdminDestination.INICIO -> onInicioClick()
                        AdminDestination.CAMPANIAS -> Unit
                        AdminDestination.MENSAJES -> onMensajesClick()
                        AdminDestination.CONFIGURACION -> onConfiguracionClick()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCampaniaClick,
                shape = RoundedCornerShape(22.dp),
                containerColor = TealPrimary,
                contentColor = Surface
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva campaña")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Button(
                onClick = onGestionarPublicacionesClick,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Editar todas mis publicaciones", color = Surface)
            }

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealPrimary)
                }
                !error.isNullOrBlank() -> Text(error, color = BrownPrimary, fontSize = 14.sp)
                campaigns.isEmpty() -> Text(
                    "Aún no tienes campañas.",
                    color = TextSecondary,
                    fontSize = 15.sp
                )
                else -> campaigns.forEach { campaign ->
                    CampaignManagementCard(
                        campaign = campaign,
                        onEditClick = { onEditarCampaniaClick(campaign.id) },
                        onParticipantsClick = onGestionCampaniaClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
private fun CampaignManagementCard(
    campaign: CampaignResponse,
    onEditClick: () -> Unit,
    onParticipantsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                campaign.title,
                color = TealPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Organización: ${campaign.organization_name}", color = TextSecondary)
            Text("Creada: ${campaign.created_at.take(10)}", color = TextSecondary)
            Text(
                "Estado: ${campaign.status.replaceFirstChar { it.uppercase() }}",
                color = TextSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar campaña", tint = TealPrimary)
                }
                IconButton(onClick = onParticipantsClick) {
                    Icon(Icons.Default.Group, contentDescription = "Participantes", tint = TealPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MisCampaniasAdminPreview() {
    FamiliasQueSumanTheme(darkTheme = false) { MisCampaniasScreen() }
}
