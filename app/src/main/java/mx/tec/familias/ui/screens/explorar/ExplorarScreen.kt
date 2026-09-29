package mx.tec.familias.ui.screens.explorar

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.components.BottomNavigationBar
import mx.tec.familias.ui.components.FamilyDestination
import mx.tec.familias.ui.theme.Background
import mx.tec.familias.ui.theme.BrownPrimary
import mx.tec.familias.ui.theme.Divider
import mx.tec.familias.ui.theme.OrangePrimary
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealDark
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextPrimary
import mx.tec.familias.ui.theme.TextSecondary
import androidx.compose.material3.FilterChipDefaults

@Composable
fun ExplorarScreen(
    onInicioClick: () -> Unit = {},
    onActividadesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onCampaniaClick: () -> Unit = {},
    onActividadClick: () -> Unit = {}
) {

    var searchText by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 32.dp
            )
        ) {

            item {
                ExplorarHeader(
                    onProfileClick = onPerfilClick
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = "Explorar",
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        placeholder = {
                            Text(
                                text = "Buscar causas, asociaciones..."
                            )
                        },

                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = TealPrimary
                            )
                        },

                        singleLine = true,

                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    ExplorarFilters()

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    Text(
                        text = "Campañas",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    CampaignCard(
                        tag = "URGENTE",
                        tagColor = BrownPrimary,
                        title = "Útiles Escolares para Todos",
                        organization = "Fundación Aprender Juntos",
                        description = "Ayuda a que niñas y niños comiencen el ciclo escolar con todo lo necesario.",
                        progress = 0.80f,
                        progressText = "80%",
                        buttonText = "Ver campaña",
                        onClick = onCampaniaClick
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    CampaignCard(
                        tag = "DONACIÓN",
                        tagColor = TealDark,
                        title = "Canasta Solidaria de Invierno",
                        organization = "Asociación Manos Unidas",
                        description = "Apoya a familias de la comunidad con alimentos y productos básicos.",
                        progress = 0.45f,
                        progressText = "45%",
                        buttonText = "Ver campaña",
                        onClick = onCampaniaClick
                    )

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )

                    Text(
                        text = "Actividades",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    ActivityCard(
                        title = "Plantación de Árboles en El Pardo",
                        organization = "Asociación Bosque Vivo",
                        participants = "5 familias inscritas",
                        date = "Sábado 24 de mayo",
                        onClick = onActividadClick
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    ActivityCard(
                        title = "Lectura Compartida",
                        organization = "Fundación Aprender Juntos",
                        participants = "2 familias inscritas",
                        date = "Domingo 25 de mayo",
                        onClick = onActividadClick
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
            }
        }

        BottomNavigationBar(
            currentDestination = FamilyDestination.EXPLORAR,

            onDestinationSelected = { destination ->

                when (destination) {

                    FamilyDestination.INICIO ->
                        onInicioClick()

                    FamilyDestination.EXPLORAR ->
                        Unit

                    FamilyDestination.ACTIVIDADES ->
                        onActividadesClick()

                    FamilyDestination.PERFIL ->
                        onPerfilClick()
                }
            }
        )
    }
}

@Composable
private fun ExplorarHeader(
    onProfileClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = TealPrimary
            )
        }

        Text(
            text = "Familias que Suman +",

            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),

            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimary
        )

        IconButton(
            onClick = onProfileClick
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TealDark),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun ExplorarFilters() {

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),

        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        FilterChip(
            selected = true,
            onClick = {},

            label = {
                Text(
                    text = "Todos"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangePrimary,
                selectedLabelColor = TextPrimary,
                containerColor = Surface,
                labelColor = TextPrimary
            ),
            shape = RoundedCornerShape(50)
        )

        FilterChip(
            selected = false,
            onClick = {},

            label = {
                Text(
                    text = "Voluntariado"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangePrimary,
                selectedLabelColor = TextPrimary,
                containerColor = Surface,
                labelColor = TextPrimary
            ),
            shape = RoundedCornerShape(50)
        )

        FilterChip(
            selected = false,
            onClick = {},

            label = {
                Text(
                    text = "Donaciones"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangePrimary,
                selectedLabelColor = TextPrimary,
                containerColor = Surface,
                labelColor = TextPrimary
            ),
            shape = RoundedCornerShape(50)
        )

        FilterChip(
            selected = false,
            onClick = {},

            label = {
                Text(
                    text = "Eventos"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangePrimary,
                selectedLabelColor = TextPrimary,
                containerColor = Surface,
                labelColor = TextPrimary
            ),
            shape = RoundedCornerShape(50)
        )

        FilterChip(
            selected = false,
            onClick = {},

            label = {
                Text(
                    text = "Talleres"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangePrimary,
                selectedLabelColor = TextPrimary,
                containerColor = Surface,
                labelColor = TextPrimary
            ),
            shape = RoundedCornerShape(50)
        )
    }
}

@Composable
private fun CampaignCard(
    tag: String,
    tagColor: Color,
    title: String,
    organization: String,
    description: String,
    progress: Float,
    progressText: String,
    buttonText: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .background(Divider)
            )

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = tag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = organization,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealDark
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Progreso",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = progressText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                LinearProgressIndicator(
                    progress = { progress },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(
                            RoundedCornerShape(4.dp)
                        ),

                    color = TealPrimary,
                    trackColor = Divider
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Button(
                    onClick = onClick,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    )
                ) {

                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    organization: String,
    participants: String,
    date: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Divider)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = BrownPrimary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrownPrimary
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = title,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = organization,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealDark
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = participants,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Button(
                onClick = onClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(10.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {

                Text(
                    text = "Ver actividad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}