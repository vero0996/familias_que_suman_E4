package mx.tec.familias.data.model

data class FamilyMember(
    val id: String,
    val nombre: String,
    val fechaNacimiento: String,
    val parentesco: String,
    val edad: Int? = null
)
