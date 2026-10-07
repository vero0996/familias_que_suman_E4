package mx.tec.familias.data.repository

import mx.tec.familias.data.model.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ActividadesRepositoryTest {
    private fun evento(id: String = "e", capacidad: Int = 2) = Activity(
        id, "Evento", "Asociación", "Descripción", "24 NOV 2026", "09:00", "Parque", capacidad, 1000L
    )
    private fun repo(capacidad: Int = 2) = ActividadesRepository(listOf(evento(capacidad = capacidad)), { 0L })
    private fun inscribir(repo: ActividadesRepository, familia: String, evento: String = "e") =
        repo.inscribir(evento, familia, listOf("$familia:titular"))

    @Test fun conCupoConfirmaUnaFamilia() {
        val r = repo()
        val i = inscribir(r, "A")
        assertEquals(EstadoInscripcion.CONFIRMADA, i.estado)
        assertNull(i.ordenEspera)
        assertEquals(1, r.consultar().disponibles("e"))
    }

    @Test fun cupoLlenoAgregaAEsperaSinConsumirLugar() {
        val r = repo(1)
        inscribir(r, "A")
        val b = inscribir(r, "B")
        assertEquals(EstadoInscripcion.EN_ESPERA, b.estado)
        assertEquals(1, r.consultar().ocupados("e"))
        assertEquals(0, r.consultar().disponibles("e"))
    }

    @Test fun filaRespetaOrdenYPosicionesPorEvento() {
        val r = ActividadesRepository(listOf(evento("e", 1), evento("otro", 1)), { 0L })
        inscribir(r, "A"); inscribir(r, "A", "otro")
        val b = inscribir(r, "B")
        val x = inscribir(r, "X", "otro")
        val c = inscribir(r, "C")
        assertEquals(1, r.consultar().posicion(b))
        assertEquals(1, r.consultar().posicion(x))
        assertEquals(2, r.consultar().posicion(c))
        assertTrue(b.ordenEspera!! < c.ordenEspera!!)
    }

    @Test fun dobleInscripcionConfirmadaEsIdempotente() {
        val r = repo()
        val a = inscribir(r, "A")
        assertEquals(a.id, inscribir(r, "A").id)
        assertEquals(1, r.consultar().inscripciones.size)
    }

    @Test fun dobleSolicitudEnEsperaNoDuplicaNiCambiaOrden() {
        val r = repo(1)
        inscribir(r, "A")
        val b = inscribir(r, "B")
        inscribir(r, "C")
        assertEquals(b, inscribir(r, "B"))
        assertEquals(3, r.consultar().inscripciones.size)
        assertEquals(1, r.consultar().posicion(b))
    }

    @Test fun integrantesNoMultiplicanElCupoDeFamilia() {
        val r = repo(1)
        val participantes = mutableListOf("titular", "hijo", "hijo")
        val i = r.inscribir("e", "A", participantes, "  Sin nueces  ")
        participantes.clear()
        assertEquals(listOf("titular", "hijo"), i.participantesIds)
        assertEquals("Sin nueces", i.observaciones)
        assertEquals(1, r.consultar().ocupados("e"))
    }

    @Test fun mismaFamiliaPuedeInscribirseEnDosEventos() {
        val r = ActividadesRepository(listOf(evento("e"), evento("otro")), { 0L })
        inscribir(r, "A"); inscribir(r, "A", "otro")
        assertEquals(2, r.consultar().inscripciones.size)
        assertEquals(1, r.consultar().ocupados("otro"))
    }

    @Test fun solicitudesInvalidasNoCambianDatos() {
        val r = repo()
        assertThrows(IllegalArgumentException::class.java) { r.inscribir("e", "A", emptyList()) }
        assertThrows(IllegalArgumentException::class.java) { r.inscribir("e", "", listOf("titular")) }
        assertThrows(IllegalArgumentException::class.java) { inscribir(r, "A", "inexistente") }
        assertEquals(0, r.consultar().inscripciones.size)
    }

    @Test fun cierreRechazaNuevasSolicitudes() {
        var ahora = 0L
        val r = ActividadesRepository(listOf(evento()), { ahora })
        val a = inscribir(r, "A")
        ahora = 1000L
        assertThrows(IllegalArgumentException::class.java) { inscribir(r, "B") }
        assertEquals(a.id, inscribir(r, "A").id)
        assertEquals(1, r.consultar().inscripciones.size)
    }

    @Test fun concurrenciaNoSobrepasaCupoNiDuplicaFamilias() {
        val r = repo(3)
        val pool = Executors.newFixedThreadPool(8)
        val salida = CountDownLatch(1)
        val tareas = (0 until 80).map { n -> pool.submit {
            salida.await()
            inscribir(r, "familia:${n % 40}")
        } }
        salida.countDown()
        try {
            tareas.forEach { it.get(10, TimeUnit.SECONDS) }
            val datos = r.consultar()
            assertEquals(3, datos.ocupados("e"))
            assertEquals(37, datos.inscripciones.count { it.estado == EstadoInscripcion.EN_ESPERA })
            assertEquals(40, datos.inscripciones.size)
            assertEquals(37, datos.inscripciones.mapNotNull { it.ordenEspera }.distinct().size)
        } finally { pool.shutdownNow() }
    }

    @Test fun consultarNoInscribeAutomaticamenteAQuienEspera() {
        val r = repo(1)
        inscribir(r, "A")
        val b = inscribir(r, "B")
        repeat(3) { assertEquals(b, r.consultar().inscripciones.first { it.id == b.id }) }
    }

    @Test fun cancelarLiberaCupoSinCambiarOtroEvento() {
        val r = ActividadesRepository(listOf(evento("e", 1), evento("otro", 1)), { 0L })
        val a = inscribir(r, "A")
        val otro = inscribir(r, "A", "otro")
        assertTrue(r.cancelar(a.id, "A"))
        assertEquals(1, r.consultar().disponibles("e"))
        assertEquals(otro, r.consultar().inscripciones.single())
    }

    @Test fun cancelarPromuevePrimeraFamiliaYRecalculaPosiciones() {
        val r = repo(1)
        val a = inscribir(r, "A")
        val b = r.inscribir("e", "B", listOf("titular", "hijo"), "Observación")
        val c = inscribir(r, "C")
        assertTrue(r.cancelar(a.id, "A"))
        val datos = r.consultar()
        val promovida = datos.inscripciones.first { it.id == b.id }
        assertEquals(EstadoInscripcion.CONFIRMADA, promovida.estado)
        assertEquals(b.participantesIds, promovida.participantesIds)
        assertEquals(b.observaciones, promovida.observaciones)
        assertEquals(1, datos.posicion(c))
        assertEquals(1, datos.ocupados("e"))
    }

    @Test fun dobleCancelacionNoLiberaDosLugares() {
        val r = repo(1)
        val a = inscribir(r, "A")
        val b = inscribir(r, "B")
        val c = inscribir(r, "C")
        assertTrue(r.cancelar(a.id, "A"))
        assertFalse(r.cancelar(a.id, "A"))
        assertEquals(EstadoInscripcion.CONFIRMADA, r.consultar().inscripciones.first { it.id == b.id }.estado)
        assertEquals(EstadoInscripcion.EN_ESPERA, r.consultar().inscripciones.first { it.id == c.id }.estado)
    }

    @Test fun cancelarOtraFamiliaFallaSinModificarDatos() {
        val r = repo()
        val a = inscribir(r, "A")
        assertThrows(IllegalArgumentException::class.java) { r.cancelar(a.id, "B") }
        assertEquals(a, r.consultar().inscripciones.single())
    }

    @Test fun noPromueveDespuesDelCierre() {
        var ahora = 0L
        val r = ActividadesRepository(listOf(evento(capacidad = 1)), { ahora })
        val a = inscribir(r, "A")
        val b = inscribir(r, "B")
        ahora = 1000L
        r.cancelar(a.id, "A")
        assertEquals(b, r.consultar().inscripciones.single())
    }

    @Test fun familiaCanceladaSeReincorporaAlFinalDeLaFila() {
        val r = repo(1)
        val a = inscribir(r, "A")
        inscribir(r, "B")
        val c = inscribir(r, "C")
        r.cancelar(a.id, "A")
        val nueva = inscribir(r, "A")
        assertNotEquals(a.id, nueva.id)
        assertEquals(EstadoInscripcion.EN_ESPERA, nueva.estado)
        assertEquals(1, r.consultar().posicion(c))
        assertEquals(2, r.consultar().posicion(nueva))
    }

    @Test fun cancelacionesConcurrentesRespetanCupoYOrden() {
        val r = repo(2)
        val a = inscribir(r, "A")
        val b = inscribir(r, "B")
        val c = inscribir(r, "C")
        val d = inscribir(r, "D")
        val e = inscribir(r, "E")
        val pool = Executors.newFixedThreadPool(2)
        try {
            val t1 = pool.submit { r.cancelar(a.id, "A") }
            val t2 = pool.submit { r.cancelar(b.id, "B") }
            t1.get(10, TimeUnit.SECONDS); t2.get(10, TimeUnit.SECONDS)
            val datos = r.consultar()
            assertEquals(setOf(c.id, d.id), datos.inscripciones.filter {
                it.estado == EstadoInscripcion.CONFIRMADA
            }.map { it.id }.toSet())
            assertEquals(2, datos.ocupados("e"))
            assertEquals(1, datos.posicion(e))
        } finally { pool.shutdownNow() }
    }

}