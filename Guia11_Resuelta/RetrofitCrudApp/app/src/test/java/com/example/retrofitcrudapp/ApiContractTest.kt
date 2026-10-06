package com.example.retrofitcrudapp

import com.google.gson.JsonParser
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiContractTest {
    private lateinit var server: MockWebServer
    private lateinit var alumnos: AlumnoApi
    private lateinit var profesores: ProfesorApi
    @Before fun preparar() {
        server = MockWebServer()
        server.start()
        val retrofit = Retrofit.Builder().baseUrl(server.url("/api/"))
            .addConverterFactory(GsonConverterFactory.create(ApiJson.gson)).build()
        alumnos = retrofit.create(AlumnoApi::class.java)
        profesores = retrofit.create(ProfesorApi::class.java)
    }
    @After fun cerrar() { server.shutdown() }
    private val json = """{"id":"7","nombre":"Ana","apellido":"LÃ³pez","edad":21}"""
    private fun responder(body: String = json, code: Int = 200) {
        server.enqueue(MockResponse().setResponseCode(code).setHeader("Content-Type", "application/json").setBody(body))
    }
    private fun verificar(method: String, path: String, body: Boolean = false) {
        val request = server.takeRequest(2, TimeUnit.SECONDS)!!
        assertEquals(method, request.method)
        assertEquals("/api/escuela/$path", request.path)
        if (body) {
            val objeto = JsonParser().parse(request.body.readUtf8()).asJsonObject
            assertEquals("Ana", objeto["nombre"].asString)
            assertEquals("LÃ³pez", objeto["apellido"].asString)
            assertEquals(21, objeto["edad"].asInt)
        }
    }
    @Test fun contratoCrudAlumnos() {
        responder("[$json]")
        assertEquals(7, alumnos.obtenerAlumnos().execute().body()!!.single().id)
        verificar("GET", "alumno")
        responder()
        assertEquals("Ana", alumnos.obtenerAlumnoPorId(7).execute().body()!!.nombre)
        verificar("GET", "alumno/7")
        responder(code = 201)
        assertTrue(alumnos.crearAlumno(Alumno(0, "Ana", "LÃ³pez", 21)).execute().isSuccessful)
        verificar("POST", "alumno", true)
        responder()
        assertTrue(alumnos.actualizarAlumno(7, Alumno(7, "Ana", "LÃ³pez", 21)).execute().isSuccessful)
        verificar("PUT", "alumno/7", true)
        responder("", 204)
        assertTrue(alumnos.eliminarAlumno(7).execute().isSuccessful)
        verificar("DELETE", "alumno/7")
    }
    @Test fun contratoCrudProfesores() {
        responder("[$json]")
        assertEquals(7, profesores.obtenerProfesores().execute().body()!!.single().id)
        verificar("GET", "profesor")
        responder()
        assertEquals("Ana", profesores.obtenerProfesorPorId(7).execute().body()!!.nombre)
        verificar("GET", "profesor/7")
        responder(code = 201)
        assertTrue(profesores.crearProfesor(Profesor(0, "Ana", "LÃ³pez", 21)).execute().isSuccessful)
        verificar("POST", "profesor", true)
        responder()
        assertTrue(profesores.actualizarProfesor(7, Profesor(7, "Ana", "LÃ³pez", 21)).execute().isSuccessful)
        verificar("PUT", "profesor/7", true)
        responder("", 204)
        assertTrue(profesores.eliminarProfesor(7).execute().isSuccessful)
        verificar("DELETE", "profesor/7")
    }
    @Test fun errorHttpNoSeConsideraExito() {
        responder("{\"message\":\"Not found\"}", 404)
        val response = profesores.obtenerProfesores().execute()
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test fun edadesInvalidasNoOcultanLosProfesores() {
        responder("""[{"id":"1","nombre":"Nombre","apellido":"Apellido","edad":"edad 1"},{"id":"2","nombre":"Ana","apellido":"López","edad":"35"}]""")
        val lista = profesores.obtenerProfesores().execute().body()!!
        assertEquals(2, lista.size)
        assertNull(lista[0].edad)
        assertEquals(35, lista[1].edad)
    }
    @Test fun edadesAusentesNoSeInventan() {
        responder("""[{"id":"1","nombre":null,"apellido":"Apellido","edad":null},{"id":"2","nombre":"Ana","edad":-2},{"id":"3","nombre":"Eva","edad":18.5}]""")
        val lista = alumnos.obtenerAlumnos().execute().body()!!
        assertEquals(3, lista.size)
        assertTrue(lista.all { it.edad == null })
        assertEquals("", lista[0].nombre)
    }
    @Test fun idInvalidoNoPermiteEditarOtroRegistro() {
        responder("""{"id":"invalido","nombre":"Ana","edad":22}""")
        try {
            profesores.obtenerProfesorPorId(1).execute()
            fail("Se esperaba rechazar el ID inválido")
        } catch (expected: com.google.gson.JsonParseException) {
            assertTrue(expected.message!!.contains("ID"))
        }
    }
}
