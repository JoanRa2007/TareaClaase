package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobil.domain.model.Sesion

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long,
    val username: String,
    val nombreCompleto: String,
    val roles: List<String> = emptyList(),
    val permisos: List<String> = emptyList()
)

@Serializable
data class UsuarioMeDto(
    val id: Long? = null,
    val username: String,
    val empleadoId: Long? = null,
    val empleadoNombre: String? = null,
    val cargo: String? = null,
    val roles: List<String> = emptyList(),
    val permisos: List<String> = emptyList(),
    val estado: Boolean? = null,
    val fechaCreacion: String? = null
)

fun LoginResponseDto.aSesion(): Sesion = Sesion(
    token = token,
    usuario = username,
    nombreCompleto = nombreCompleto,
    permisos = permisos.toSet()
)

fun UsuarioMeDto.aSesion(token: String): Sesion = Sesion(
    token = token,
    usuario = username,
    nombreCompleto = empleadoNombre ?: username,
    permisos = permisos.toSet()
)
