package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.platform.formatearSoles

fun Producto.comoTextoParaCompartir(): String {
    return "💊 Producto: $nombre\n💰 Precio: ${formatearSoles(precio)}\n📦 Stock: $stock unidades\nEstado: ${if (activo) "Activo" else "Inactivo"}"
}
