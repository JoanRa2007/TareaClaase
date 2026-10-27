package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobil.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobil.domain.model.SesionException

@PublishedApi
@Serializable
internal data class ErrorBodyDto(
    val message: String? = null
)

fun mapearErrorSesion(
    estado: HttpStatusCode,
    mensaje: String?,
    esLogin: Boolean = false
): ErrorSesion {
    return when (estado) {
        HttpStatusCode.Unauthorized -> {
            if (esLogin) {
                if (mensaje?.contains("expirado", ignoreCase = true) == true) {
                    ErrorSesion.SesionExpirada
                } else {
                    ErrorSesion.CredencialesInvalidas
                }
            } else {
                ErrorSesion.SesionExpirada
            }
        }
        HttpStatusCode.Forbidden -> ErrorSesion.SinPermiso
        else -> ErrorSesion.SinConexion
    }
}

suspend inline fun <T> ejecutarConMapeoError(
    esLogin: Boolean = false,
    crossinline block: suspend () -> T
): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: SesionException) {
        throw e
    } catch (e: ResponseException) {
        val mensaje = try {
            e.response.body<ErrorBodyDto>().message
        } catch (_: Exception) {
            null
        }
        val error = mapearErrorSesion(e.response.status, mensaje, esLogin)
        throw SesionException(error)
    } catch (_: Throwable) {
        throw SesionException(ErrorSesion.SinConexion)
    }
}
