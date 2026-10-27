package pe.edu.upeu.pharmamobil.domain.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.pharmamobil.domain.model.Sesion

class SesionManager {
    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    fun iniciar(sesion: Sesion) {
        _sesion.value = sesion
        _aviso.value = null
    }

    fun cerrar() {
        _sesion.value = null
        _aviso.value = null
    }

    fun expirar(aviso: String? = null) {
        _sesion.value = null
        _aviso.value = aviso ?: "Tu sesión expiró. Inicia sesión nuevamente"
    }

    fun limpiarAviso() {
        _aviso.value = null
    }
}
