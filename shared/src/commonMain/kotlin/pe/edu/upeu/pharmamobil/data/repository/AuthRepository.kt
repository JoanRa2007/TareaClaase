package pe.edu.upeu.pharmamobil.data.repository

import pe.edu.upeu.pharmamobil.data.remote.AuthApi
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginRequest
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginResponse

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, pass: String): LoginResponse {
        val response = authApi.login(LoginRequest(username, pass))
        tokenManager.guardarToken(response.token)
        return response
    }

    suspend fun obtenerUsuarioActual(): LoginResponse {
        return authApi.obtenerUsuarioActual()
    }

    fun cerrarSesion() {
        tokenManager.limpiarToken()
    }
}
