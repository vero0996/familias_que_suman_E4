package mx.tec.familias.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.tec.familias.ui.theme.Surface
import mx.tec.familias.ui.theme.TealLight
import mx.tec.familias.ui.theme.TealPrimary
import mx.tec.familias.ui.theme.TextSecondary

enum class FamilyDestination {
    INICIO,
    EXPLORAR,
    ACTIVIDADES,
    MENSAJES,
    PERFIL
}

@Composable
fun BottomNavigationBar(
    currentDestination: FamilyDestination,
    onDestinationSelected: (FamilyDestination) -> Unit,
    mostrarMensajes: Boolean = false
) {
    NavigationBar(
        containerColor = Surface.copy(alpha = 0.92f),
        tonalElevation = 3.dp,
        modifier = Modifier.padding(
            start = 12.dp,
            end = 12.dp,
            bottom = 8.dp
        )
    ) {

        NavigationBarItem(
            selected = currentDestination == FamilyDestination.INICIO,
            onClick = {
                onDestinationSelected(FamilyDestination.INICIO)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            },
            colors = navigationColors()
        )

        NavigationBarItem(
            selected = currentDestination == FamilyDestination.EXPLORAR,
            onClick = {
                onDestinationSelected(FamilyDestination.EXPLORAR)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Explorar"
                )
            },
            label = {
                Text("Explorar")
            },
            colors = navigationColors()
        )

        NavigationBarItem(
            selected = currentDestination == FamilyDestination.ACTIVIDADES,
            onClick = {
                onDestinationSelected(FamilyDestination.ACTIVIDADES)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Actividades"
                )
            },
            label = {
                Text("Actividades")
            },
            colors = navigationColors()
        )

        if (mostrarMensajes) {
            NavigationBarItem(
                selected = currentDestination == FamilyDestination.MENSAJES,
                onClick = {
                    onDestinationSelected(FamilyDestination.MENSAJES)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Mensajes"
                    )
                },
                label = {
                    Text("Mensajes")
                },
                colors = navigationColors()
            )
        }

        NavigationBarItem(
            selected = currentDestination == FamilyDestination.PERFIL,
            onClick = {
                onDestinationSelected(FamilyDestination.PERFIL)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text("Perfil")
            },
            colors = navigationColors()
        )
    }
}

@Composable
private fun navigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = TealPrimary,
        selectedTextColor = TealPrimary,
        indicatorColor = TealLight.copy(alpha = 0.65f),
        unselectedIconColor = TextSecondary,
        unselectedTextColor = TextSecondary
    )