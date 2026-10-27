package pe.edu.upeu.pharmamobil.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginRequestDto
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginResponseDto
import pe.edu.upeu.pharmamobil.data.remote.dto.UsuarioMeDto
import pe.edu.upeu.pharmamobil.data.remote.dto.aSesion
import pe.edu.upeu.pharmamobil.data.remote.ejecutarConMapeoError
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.domain.model.SesionException
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

class AuthRepositorioRest(
    private val cliente: HttpClient,
    private val almacen: AlmacenSeguro
) : AuthRepository {

    override suspend fun iniciarSesion(credenciales: Credenciales): Sesion {
        return ejecutarConMapeoError(esLogin = true) {
            val response: LoginResponseDto = cliente.post("auth/login") {
                setBody(
                    LoginRequestDto(
                        username = credenciales.usuario,
                        password = credenciales.clave
                    )
                )
            }.body()

            almacen.guardarToken(response.token)
            cliente.authProvider<BearerAuthProvider>()?.clearToken()
            response.aSesion()
        }
    }

    override suspend fun cerrarSesion() {
        almacen.borrarToken()
        cliente.authProvider<BearerAuthProvider>()?.clearToken()
    }

    override suspend fun sesionGuardada(): Sesion? {
        val token = almacen.leerToken() ?: return null
        return try {
            ejecutarConMapeoError(esLogin = false) {
                val dto: UsuarioMeDto = cliente.get("auth/me").body()
                dto.aSesion(token)
            }
        } catch (e: SesionException) {
            if (e.error == ErrorSesion.SesionExpirada) {
                cerrarSesion()
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun probar403() {
        ejecutarConMapeoError(esLogin = false) {
            cliente.get("clientes")
        }
    }

    override fun invalidarTokenPrueba() {
        almacen.guardarToken("token_invalido_basura")
        cliente.authProvider<BearerAuthProvider>()?.clearToken()
    }
}
