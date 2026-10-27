package pe.edu.upeu.pharmamobil.domain.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobil.domain.model.SesionException
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Usuario y contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.iniciarSesion(
                    Credenciales(
                        usuario = state.username.trim(),
                        clave = state.password
                    )
                )
                _uiState.update { it.copy(isLoading = false, success = true) }
                onSuccess()
            } catch (e: SesionException) {
                val mensajeError = when (e.error) {
                    ErrorSesion.CredencialesInvalidas -> "Usuario o contraseña incorrectos"
                    ErrorSesion.SesionExpirada -> "La sesión ha expirado"
                    ErrorSesion.SinPermiso -> "No tiene permisos para realizar esta operación"
                    ErrorSesion.SinConexion -> "Error de conexión. Intente nuevamente"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mensajeError
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
