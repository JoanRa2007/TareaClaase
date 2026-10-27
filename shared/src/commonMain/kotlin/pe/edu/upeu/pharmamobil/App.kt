package pe.edu.upeu.pharmamobil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pe.edu.upeu.pharmamobil.domain.presentation.auth.LoginScreen
import pe.edu.upeu.pharmamobil.domain.presentation.auth.LoginViewModel
import pe.edu.upeu.pharmamobil.domain.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.domain.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.domain.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.domain.presentation.producto.ProductoViewModel
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.session.SesionManager
import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme

@Composable
fun App() {
    val sesionManager = koinInject<SesionManager>()
    val authRepository = koinInject<AuthRepository>()
    val sesionActual by sesionManager.sesion.collectAsState()

    val loginViewModel = koinViewModel<LoginViewModel>()
    val loginUiState by loginViewModel.uiState.collectAsState()

    var darkTheme by remember { mutableStateOf(false) }

    PharmaMobilTheme(darkTheme = darkTheme) {
        if (loginUiState.cargandoSesion) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@PharmaMobilTheme
        }

        val sesion = sesionActual
        if (sesion == null) {
            LoginScreen(
                viewModel = loginViewModel,
                sesionManager = sesionManager
            )
            return@PharmaMobilTheme
        }

        var pantallaActual by remember {
            mutableStateOf<Screen>(Screen.Inicio)
        }

        if (pantallaActual is Screen.Clientes && !sesion.puede("CLIENTES_LEER")) {
            pantallaActual = Screen.Inicio
        }

        val drawerState = rememberDrawerState(
            initialValue = DrawerValue.Closed
        )

        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerHeader(usuarioNombre = sesion.nombreCompleto.ifBlank { sesion.usuario })

                    NavigationDrawerItem(
                        label = { Text("Inicio") },
                        selected = pantallaActual is Screen.Inicio,
                        onClick = {
                            pantallaActual = Screen.Inicio
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio"
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Productos") },
                        selected = pantallaActual is Screen.Productos,
                        onClick = {
                            pantallaActual = Screen.Productos
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = "Productos"
                            )
                        }
                    )

                    if (sesion.puede("CLIENTES_LEER")) {
                        NavigationDrawerItem(
                            label = { Text("Clientes") },
                            selected = pantallaActual is Screen.Clientes,
                            onClick = {
                                pantallaActual = Screen.Clientes
                                scope.launch { drawerState.close() }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Clientes"
                                )
                            }
                        )
                    }

                    NavigationDrawerItem(
                        label = { Text("Pedidos") },
                        selected = pantallaActual is Screen.Pedidos,
                        onClick = {
                            pantallaActual = Screen.Pedidos
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Pedidos"
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                authRepository.cerrarSesion()
                                sesionManager.cerrar()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Modo oscuro")
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { darkTheme = it }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = tituloPantalla(pantallaActual)) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                when (pantallaActual) {
                    Screen.Inicio -> InicioScreen()
                    Screen.Productos -> {
                        Column(modifier = Modifier.padding(paddingValues)) {
                            val productoViewModel = koinViewModel<ProductoViewModel>()
                            ProductoScreen(viewModel = productoViewModel)
                        }
                    }
                    Screen.Clientes -> {
                        Column(modifier = Modifier.padding(paddingValues)) {
                            ClienteScreen()
                        }
                    }
                    Screen.Pedidos -> {
                        Column(modifier = Modifier.padding(paddingValues)) {
                            Text("Pantalla de pedidos en construcción")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(usuarioNombre: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "PharmaMobil",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = usuarioNombre,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun tituloPantalla(screen: Screen): String {
    return when (screen) {
        Screen.Inicio -> "Inicio"
        Screen.Productos -> "Productos"
        Screen.Clientes -> "Clientes"
        Screen.Pedidos -> "Pedidos"
    }
}
