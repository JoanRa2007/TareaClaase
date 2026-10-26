package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginRequest
import pe.edu.upeu.pharmamobil.data.remote.dto.LoginResponse

class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse =
        client.post("auth/login") {
            setBody(request)
        }.body()

    suspend fun obtenerUsuarioActual(): LoginResponse =
        client.get("auth/me").body()
}
