package mx.tec.familias.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    showProfile: Boolean = false,
    onProfileClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                TextButton(onClick = onBackClick) {
                    Text(text = "←")
                }
            }
        },
        actions = {
            if (showProfile && onProfileClick != null) {
                TextButton(onClick = onProfileClick) {
                    Text(text = "Perfil")
                }
            }
        }
    )
}
