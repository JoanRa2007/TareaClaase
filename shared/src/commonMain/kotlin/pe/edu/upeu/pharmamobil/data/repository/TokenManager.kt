package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    fun guardarToken(nuevoToken: String) {
        _token.value = nuevoToken
    }

    fun obtenerToken(): String? = _token.value

    fun limpiarToken() {
        _token.value = null
    }
}
