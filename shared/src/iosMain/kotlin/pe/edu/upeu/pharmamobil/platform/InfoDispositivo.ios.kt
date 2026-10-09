package pe.edu.upeu.pharmamobil.platform

actual fun obtenerInfoDispositivo(): String = "iOS " + platform.UIKit.UIDevice.currentDevice.systemVersion
