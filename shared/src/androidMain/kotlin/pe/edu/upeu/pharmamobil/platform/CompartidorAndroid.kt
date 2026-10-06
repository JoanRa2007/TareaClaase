package pe.edu.upeu.pharmamobil.platform

import android.content.Context
import android.content.Intent
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor

class CompartidorAndroid(private val context: Context) : Compartidor {
    override fun compartir(texto: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Compartir producto").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
