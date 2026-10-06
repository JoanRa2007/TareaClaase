package pe.edu.upeu.pharmamobil.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor

class CompartidorIos : Compartidor {
    override fun compartir(texto: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(texto),
            applicationActivities = null
        )
        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootController?.presentViewController(activityViewController, animated = true, completion = null)
    }
}
