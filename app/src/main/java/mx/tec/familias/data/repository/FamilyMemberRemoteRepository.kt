package mx.tec.familias.data.repository

import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.FamilyMemberCreateRequest
import mx.tec.familias.data.remote.FamilyMemberResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FamilyMemberRemoteRepository {

    private val api = ApiClient.apiService

    suspend fun getFamilyMembers(token: String): List<FamilyMember> {
        return api.getFamilyMembers("Bearer $token")
            .filter { it.isActive }
            .map { it.toFamilyMember() }
    }

    suspend fun createFamilyMember(
        token: String,
        nombre: String,
        fechaNacimiento: String,
        parentesco: String
    ): FamilyMember {
        val fechaIso = convertirAFechaIso(fechaNacimiento)
            ?: throw IllegalArgumentException(
                "La fecha de nacimiento no es válida. Usa dd/MM/yyyy."
            )

        val edad = calcularEdad(fechaIso)
            ?: throw IllegalArgumentException(
                "La fecha de nacimiento debe ser válida y no puede estar en el futuro."
            )

        val request = FamilyMemberCreateRequest(
            fullName = nombre.trim(),
            dateOfBirth = fechaIso,
            age = edad,
            relationshipType = parentesco.trim()
        )

        return api.createFamilyMember(
            authorization = "Bearer $token",
            request = request
        ).toFamilyMember()
    }

    suspend fun updateFamilyMember(
        token: String,
        id: String,
        nombre: String,
        fechaNacimiento: String,
        parentesco: String
    ): FamilyMember {
        val memberId = id.toIntOrNull()
            ?: throw IllegalArgumentException(
                "El identificador del integrante no es válido."
            )

        val fechaIso = convertirAFechaIso(fechaNacimiento)
            ?: throw IllegalArgumentException(
                "La fecha de nacimiento no es válida. Usa dd/MM/yyyy."
            )

        val edad = calcularEdad(fechaIso)
            ?: throw IllegalArgumentException(
                "La fecha de nacimiento debe ser válida y no puede estar en el futuro."
            )

        val request = FamilyMemberCreateRequest(
            fullName = nombre.trim(),
            dateOfBirth = fechaIso,
            age = edad,
            relationshipType = parentesco.trim()
        )

        return api.updateFamilyMember(
            authorization = "Bearer $token",
            memberId = memberId,
            request = request
        ).toFamilyMember()
    }

    private fun FamilyMemberResponse.toFamilyMember() = FamilyMember(
        id = id.toString(),
        nombre = fullName,
        fechaNacimiento = convertirFechaParaMostrar(dateOfBirth),
        parentesco = relationshipType,
        edad = age
    )

    private fun convertirFechaParaMostrar(fechaIso: String?): String {
        if (fechaIso.isNullOrBlank()) return ""

        return runCatching {
            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
                isLenient = false
            }.parse(fechaIso) ?: return fechaIso

            SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(fecha)
        }.getOrDefault(fechaIso)
    }



    private fun convertirAFechaIso(fecha: String): String? {
        val entrada = fecha.trim()
        val formatos = listOf("dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy")

        for (formato in formatos) {
            val parser = SimpleDateFormat(formato, Locale.ROOT).apply {
                isLenient = false
            }

            val posicion = java.text.ParsePosition(0)
            val fechaParseada = parser.parse(entrada, posicion)

            if (fechaParseada != null && posicion.index == entrada.length) {
                val fechaNormalizada = parser.format(fechaParseada)

                // Rechaza fechas que no coincidan exactamente con el formato.
                if (fechaNormalizada == entrada) {
                    return SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                        .format(fechaParseada)
                }
            }
        }

        return null
    }


    private fun calcularEdad(fechaIso: String): Int? {
        val nacimiento = runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
                isLenient = false
            }.parse(fechaIso)
        }.getOrNull() ?: return null

        val hoy = Calendar.getInstance()
        val fechaNacimiento = Calendar.getInstance().apply {
            time = nacimiento
        }

        if (fechaNacimiento.after(hoy)) return null

        var edad = hoy.get(Calendar.YEAR) -
                fechaNacimiento.get(Calendar.YEAR)

        if (
            hoy.get(Calendar.MONTH) < fechaNacimiento.get(Calendar.MONTH) ||
            (
                    hoy.get(Calendar.MONTH) == fechaNacimiento.get(Calendar.MONTH) &&
                            hoy.get(Calendar.DAY_OF_MONTH) <
                            fechaNacimiento.get(Calendar.DAY_OF_MONTH)
                    )
        ) {
            edad--
        }

        return edad.takeIf { it in 0..120 }
    }


    suspend fun deleteFamilyMember(token: String, id: String) {
        val memberId = id.toIntOrNull()
            ?: throw IllegalArgumentException("El identificador del integrante no es válido.")

        api.deleteFamilyMember(
            authorization = "Bearer $token",
            memberId = memberId
        )
    }

}