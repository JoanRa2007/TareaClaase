package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.model.ResultadoProductos
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioEnMemoria(
    private val api: ProductoApi
) : ProductoRepository {

    override suspend fun registrar(nombre: String, precio: Double, stock: Int): Producto {
        val request = ProductoRequestDto(
            nombre = nombre,
            precio = precio,
            stock = stock,
            estado = true,
            categoriaId = 1L
        )
        val dto = api.registrarProducto(request)
        return dto.toDomain()
    }

    override suspend fun listar(): List<Producto> =
        api.obtenerProductosRemotos().contenido.map { it.toDomain() }

    suspend fun obtenerProductos(): List<Producto> = listar()

    fun observarEstados(): Flow<String> = flow {
        emit("Iniciando")
        emit("Finalizado")
    }

    fun observarProductos(): Flow<List<Producto>> = flow {
        emit(emptyList())
        emit(listar())
    }

    fun cargarProductos(): Flow<ResultadoProductos> = flow {
        emit(ResultadoProductos.Cargando)
        try {
            val lista = listar()
            emit(ResultadoProductos.Exito(lista))
        } catch (e: Exception) {
            emit(ResultadoProductos.Error(e.message ?: "Error desconocido"))
        }
    }
}
