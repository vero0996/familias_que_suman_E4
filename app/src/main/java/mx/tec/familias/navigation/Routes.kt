package mx.tec.familias.navigation

sealed class Routes(val route: String) {
    data object Registro : Routes("registro")
    data object Inicio : Routes("inicio")
    data object Integrantes : Routes("integrantes")
}
