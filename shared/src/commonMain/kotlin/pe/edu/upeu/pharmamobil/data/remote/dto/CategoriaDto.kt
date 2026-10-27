package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaDto(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val estado: Boolean? = null
)
