package pe.edu.upeu.pharmamobil.data.repository

import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto
import pe.edu.upeu.pharmamobil.data.remote.ejecutarConMapeoError
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioRest(
    private val api: ProductoApi
) : ProductoRepository {

    override suspend fun listar(): List<Producto> = ejecutarConMapeoError(esLogin = false) {
        val pagina = api.obtenerProductosRemotos()
        pagina.contenido.map { it.toDomain() }
    }

    override suspend fun registrar(nombre: String, precio: Double, stock: Int): Producto =
        ejecutarConMapeoError(esLogin = false) {
            val categorias = api.obtenerCategorias()
            val primeraCategoria = categorias.firstOrNull()
                ?: throw IllegalStateException("No hay categorías disponibles para registrar el producto")

            val request = ProductoRequestDto(
                nombre = nombre,
                precio = precio,
                stock = stock,
                estado = true,
                categoriaId = primeraCategoria.id
            )
            val dto = api.registrarProducto(request)
            dto.toDomain()
        }
}
