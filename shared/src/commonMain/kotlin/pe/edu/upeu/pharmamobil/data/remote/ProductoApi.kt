package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaDto
import pe.edu.upeu.pharmamobil.data.remote.dto.PaginaProductosDto
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoBackendDto
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto

class ProductoApi(private val client: HttpClient) {

    suspend fun obtenerProductosRemotos(page: Int = 0, size: Int = 10): PaginaProductosDto =
        client.get("productos") {
            parameter("page", page)
            parameter("size", size)
        }.body()

    suspend fun obtenerProductoPorId(id: Long): ProductoBackendDto =
        client.get("productos/$id").body()

    suspend fun registrarProducto(request: ProductoRequestDto): ProductoBackendDto =
        client.post("productos") {
            setBody(request)
        }.body()

    suspend fun obtenerCategorias(): List<CategoriaDto> =
        client.get("categorias").body()
}
