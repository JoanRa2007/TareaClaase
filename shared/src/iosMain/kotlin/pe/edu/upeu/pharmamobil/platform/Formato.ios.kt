package pe.edu.upeu.pharmamobil.platform

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSLocale

actual fun formatearSoles(valor: Double): String {
    val formatter = NSNumberFormatter().apply {
        setNumberStyle(NSNumberFormatterCurrencyStyle)
        setLocale(NSLocale(localeIdentifier = "es_PE"))
    }
    return formatter.stringFromNumber(NSNumber(double = valor)) ?: "S/ $valor"
}
