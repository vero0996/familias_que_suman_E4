package mx.tec.familias.navigation

sealed class Routes(val route: String) {
    data object SelectorRol : Routes("selectorRol")

    data object Registro : Routes("registro")

    data object Inicio : Routes("inicio")

    data object Integrantes : Routes("integrantes")

    data object Explorar : Routes("explorar")

    data object DetalleCampania : Routes("detalleCampania")

    data object DetalleActividad : Routes("detalleActividad")

    data object Inscripcion : Routes("inscripcion")

    data object ConfirmacionInscripcion : Routes("confirmacionInscripcion")

    data object Actividades : Routes("actividades")

    data object Perfil : Routes("perfil")

    data object ConfirmacionCampania : Routes("confirmacionCampania")


    // ADMIN

    data object DashboardAdmin : Routes("dashboardAdmin")

    data object MisCampanias : Routes("misCampanias")
    data object GestionPublicaciones : Routes("gestionPublicaciones")

    data object MensajesAdmin : Routes("mensajesAdmin")

    data object ChatAdmin : Routes("chatAdmin")

    data object ConfiguracionAdmin : Routes("configuracionAdmin")

    data object GestionMensajesCampania : Routes("gestionMensajesCampania")

    data object CrearCampania : Routes("crearCampania")
    data object EditarCampania : Routes("editarCampania") {
        fun createRoute(campaignId: Int) = "editarCampania/$campaignId"
    }
    data object EditarPublicacion : Routes("editarPublicacion") {
        fun createRoute(kind: String, id: Int) = "editarPublicacion/$kind/$id"
    }

    data object Mensajes : Routes("mensajes")

    data object ChatMessages : Routes("chatMessages")
    data object DetalleActividadAdmin : Routes("detalleActividadAdmin") {
        fun createRoute(activityId: Int) = "detalleActividadAdmin/$activityId"
    }
    data object GestionarParticipantesAdmin : Routes("gestionarParticipantesAdmin") {
        fun createRoute(activityId: Int) = "gestionarParticipantesAdmin/$activityId"
    }

    data object Publico : Routes("publico")
    data object Login : Routes("login")

    data object ForgotPassword : Routes("forgotPassword")

}
