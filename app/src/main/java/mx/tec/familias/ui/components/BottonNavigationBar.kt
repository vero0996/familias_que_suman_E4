package mx.tec.familias.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class FamilyDestination(val title: String) {
    INICIO("Inicio"),
    EXPLORAR("Explorar"),
    ACTIVIDADES("Actividades"),
    PERFIL("Perfil")
}

@Composable
fun BottomNavigationBar(
    currentDestination: FamilyDestination,
    onDestinationSelected: (FamilyDestination) -> Unit
) {
    NavigationBar {
        FamilyDestination.entries.forEach { destination ->
            val icon = when (destination) {
                FamilyDestination.INICIO -> Icons.Default.Home
                FamilyDestination.EXPLORAR -> Icons.Default.Search
                FamilyDestination.ACTIVIDADES -> Icons.Default.DateRange
                FamilyDestination.PERFIL -> Icons.Default.Person
            }

            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = { Icon(imageVector = icon, contentDescription = destination.title) },
                label = { Text(text = destination.title) }
            )
        }
    }
}
