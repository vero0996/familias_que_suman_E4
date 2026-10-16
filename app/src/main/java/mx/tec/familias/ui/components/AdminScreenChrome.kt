package mx.tec.familias.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreenHeader(
    title: String,
    titleMaxLines: Int = 1,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 10.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.90f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    title, color = TealPrimary, fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, lineHeight = 26.sp,
                    maxLines = titleMaxLines, overflow = TextOverflow.Ellipsis
                )
            },
            expandedHeight = maxOf(
                64.dp,
                with(LocalDensity.current) { (26.sp * titleMaxLines).toDp() } + 16.dp
            ),
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
        )
    }
}

enum class AdminDestination { INICIO, CAMPANIAS, MENSAJES, CONFIGURACION }

@Composable
fun AdminBottomNavigationBar(
    currentDestination: AdminDestination,
    onDestinationSelected: (AdminDestination) -> Unit
) {
    // Mismos márgenes, superficie y colores que BottomNavigationBar del perfil familiar.
    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp
    ) {
        val destinations = listOf(
            Triple(AdminDestination.INICIO, "Inicio", Icons.Default.Home),
            Triple(AdminDestination.CAMPANIAS, "Campañas", Icons.Default.Event),
            Triple(AdminDestination.MENSAJES, "Mensajes", Icons.AutoMirrored.Filled.Chat),
            Triple(AdminDestination.CONFIGURACION, "Configuración", Icons.Default.Settings)
        )
        destinations.forEach { (destination, label, icon) ->
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealLight.copy(alpha = 0.65f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
