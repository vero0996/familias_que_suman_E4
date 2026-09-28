package mx.tec.familias.navigation

sealed class Routes(val route: String) {
    data object Registro : Routes("registro")
    data object Inicio : Routes("inicio")
    data object Integrantes : Routes("integrantes")
    data object Explorar : Routes("explorar")
    data object DetalleCampania : Routes("detalleCampania")
    data object DetalleActividad : Routes("detalleActividad")
    data object Actividades : Routes("actividades")
    data object Perfil : Routes("perfil")
}
