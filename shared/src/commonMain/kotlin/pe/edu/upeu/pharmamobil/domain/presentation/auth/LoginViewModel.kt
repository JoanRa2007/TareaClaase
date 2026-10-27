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
import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.domain.model.SesionException
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.session.SesionManager

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sesionManager: SesionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        verificarSesionGuardada()
    }

    fun verificarSesionGuardada() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoSesion = true) }
            val sesionGuardada = authRepository.sesionGuardada()
            if (sesionGuardada != null) {
                sesionManager.iniciar(sesionGuardada)
                _uiState.update { it.copy(cargandoSesion = false, sesion = sesionGuardada) }
            } else {
                _uiState.update { it.copy(cargandoSesion = false) }
            }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "El usuario y la contraseña no pueden estar vacíos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val sesion = authRepository.iniciarSesion(
                    Credenciales(
                        usuario = state.username.trim(),
                        clave = state.password
                    )
                )
                sesionManager.iniciar(sesion)
                _uiState.update { it.copy(isLoading = false, error = null, sesion = sesion) }
            } catch (e: SesionException) {
                val mensajeError = when (e.error) {
                    ErrorSesion.CredencialesInvalidas -> "Usuario o contraseña incorrectos"
                    ErrorSesion.SinConexion -> "No hay conexión con el servidor"
                    ErrorSesion.SesionExpirada -> "Tu sesión expiró. Inicia sesión nuevamente"
                    ErrorSesion.SinPermiso -> "No tienes permisos para realizar esta operación"
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
    val cargandoSesion: Boolean = true,
    val error: String? = null,
    val sesion: Sesion? = null
)
