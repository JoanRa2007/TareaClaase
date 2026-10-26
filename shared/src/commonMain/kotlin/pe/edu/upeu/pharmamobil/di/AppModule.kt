package pe.edu.upeu.pharmamobil.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.remote.AuthApi
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobil.data.repository.AuthRepository
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.TokenManager
import pe.edu.upeu.pharmamobil.domain.presentation.auth.LoginViewModel
import pe.edu.upeu.pharmamobil.domain.presentation.producto.ProductoViewModel
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase

expect val platformModule: Module

val networkModule = module {
    single { TokenManager() }
    single<HttpClient> { crearHttpClient(get<HttpClientEngine>()) { get<TokenManager>().obtenerToken() } }
    single { AuthApi(get()) }
    single { ProductoApi(get()) }
}

val repositoryModule = module {
    single { AuthRepository(get(), get()) }
    single<ProductoRepository> { ProductoRepositorioEnMemoria(get()) }
}

val useCaseModule = module {
    factory { RegistrarProductoUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::ProductoViewModel)
    viewModelOf(::LoginViewModel)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }
}
