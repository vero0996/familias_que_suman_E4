package mx.tec.familias.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import mx.tec.familias.ui.theme.TealPrimary

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
    NavigationBar {

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
            }
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
            }
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
            }
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
                }
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
            }
        )
    }
}