package pe.edu.upeu.pharmamobil.domain.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import pe.edu.upeu.pharmamobil.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobil.domain.model.SesionException
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pharmamobil.shared.generated.resources.Res
import pharmamobil.shared.generated.resources.pharmamobil_logo

@Composable
fun InicioScreen(
    authRepository: AuthRepository = koinInject(),
    esDepuracion: Boolean = koinInject()
) {
    var mensajePrueba by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.pharmamobil_logo),
            contentDescription = "Logo PharmaMobil",
            tint = Color.Unspecified,
            modifier = Modifier.size(96.dp)
        )

        Text(
            text = "PharmaMobil",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Sistema de gestión farmacéutica",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (esDepuracion) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Panel de Pruebas (Debug)",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    mensajePrueba = null
                                    try {
                                        authRepository.probar403()
                                        mensajePrueba = "Llamada a GET clientes exitosa"
                                    } catch (e: SesionException) {
                                        mensajePrueba = when (e.error) {
                                            ErrorSesion.SinPermiso -> "No tienes permisos para realizar esta operación"
                                            ErrorSesion.SesionExpirada -> "Tu sesión expiró. Inicia sesión nuevamente"
                                            ErrorSesion.CredencialesInvalidas -> "Usuario o contraseña incorrectos"
                                            ErrorSesion.SinConexion -> "No hay conexión con el servidor"
                                        }
                                    } catch (e: Exception) {
                                        mensajePrueba = e.message ?: "Error en la prueba"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Probar 403")
                        }

                        Button(
                            onClick = {
                                authRepository.invalidarTokenPrueba()
                                mensajePrueba = "Token invalidado con éxito. Realiza cualquier acción para probar 401."
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Invalidar token")
                        }
                    }

                    mensajePrueba?.let { mensaje ->
                        Text(
                            text = mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
