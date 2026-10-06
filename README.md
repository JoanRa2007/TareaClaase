# PharmaMobil — Consumo REST con Ktor

## URL base

`https://api.escuelajs.co/api/v1/`

## Endpoint consumido

`GET products` (con parámetro de consulta `limit`)

Ejemplo: `https://api.escuelajs.co/api/v1/products?limit=10`

## Campos del DTO (`ProductoDto`)

| Campo | Tipo | Obligatorio |
|---|---|---|
| `id` | `Int` | Sí |
| `title` | `String` | Sí |
| `price` | `Double` | Sí |
| `description` | `String` | No (default `""`) |
| `images` | `List<String>` | No (default lista vacía) |
| `category` | `CategoriaDto?` | No |

`CategoriaDto`: `id: Int`, `name: String`.

## Conectividad REST

- **URL base:** `https://api.escuelajs.co/api/v1/`
- **Endpoints implementados:** `GET /products`, `GET /products/{id}`
- **Manejo de errores:** excepciones de red, timeout y deserialización se capturan en `ProductoRepositorioEnMemoria` y se exponen como `ProductoListaEstado.Error` sin cerrar la aplicación.

## Capacidades nativas (expect / actual e Inyección con Koin)

En esta sección se documenta la implementación de capacidades nativas de plataforma en Kotlin Multiplatform (KMP) correspondientes a la Guía Práctica N.º 09 de PharmaMobil:

### 1. Formateador de moneda (`expect / actual`)
Permite formatear valores numéricos a moneda peruana (Soles) adaptándose a las convenciones de cada plataforma.
- **Expect (`commonMain`):** `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.kt` (`expect fun formatearSoles(valor: Double): String`)
- **Actual (`androidMain`):** `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.android.kt` (Usa `java.text.NumberFormat` con Locale de Perú `es-PE`).
- **Actual (`iosMain`):** `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.ios.kt` (Usa `NSNumberFormatter` y `NSLocale(localeIdentifier = "es_PE")`).

### 2. Capacidad de Compartir (`Compartidor` + Koin)
Permite compartir información detallada de los productos utilizando los mecanismos nativos de compartir de cada sistema operativo (Intents en Android y UIActivityViewController en iOS).
- **Interfaz (`commonMain`):** `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/platform/Compartidor.kt` (`fun compartir(texto: String)`)
- **Extensión de Producto (`commonMain`):** `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/usecase/TextoParaCompartir.kt` (`fun Producto.comoTextoParaCompartir(): String`)
- **Implementación Android (`androidMain`):** `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorAndroid.kt` (Usa `Intent.ACTION_SEND` con `FLAG_ACTIVITY_NEW_TASK`).
- **Implementación iOS (`iosMain`):** `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorIos.kt` (Usa `UIActivityViewController`).
- **Inyección con Koin:**
  - `PlatformModule.android.kt`: `single<Compartidor> { CompartidorAndroid(androidContext()) }`
  - `PlatformModule.ios.kt`: `single<Compartidor> { CompartidorIos() }`
- **Uso en UI:** Inyectado en `ProductoViewModel` y expuesto mediante el botón de compartir (`Icons.Default.Share`) en `ProductoScreen`.
