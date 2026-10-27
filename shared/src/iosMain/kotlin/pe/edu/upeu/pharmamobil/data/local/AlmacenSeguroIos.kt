package pe.edu.upeu.pharmamobil.data.local

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSCopyingProtocol
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

class AlmacenSeguroIos : AlmacenSeguro {
    @OptIn(ExperimentalForeignApi::class)
    private fun queryBase(): NSMutableDictionary {
        val query = NSMutableDictionary()
        query.setObject(kSecClassGenericPassword as Any, forKey = kSecClass as NSCopyingProtocol)
        query.setObject("pharmamobil" as Any, forKey = kSecAttrService as NSCopyingProtocol)
        query.setObject("jwt" as Any, forKey = kSecAttrAccount as NSCopyingProtocol)
        return query
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun guardarToken(token: String) {
        borrarToken()
        val data = (token as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val query = queryBase()
        query.setObject(data, forKey = kSecValueData as NSCopyingProtocol)
        SecItemAdd(query as platform.CoreFoundation.CFDictionaryRef, null)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun leerToken(): String? {
        val query = queryBase()
        query.setObject(NSNumber(bool = true), forKey = kSecReturnData as NSCopyingProtocol)
        query.setObject(kSecMatchLimitOne as Any, forKey = kSecMatchLimit as NSCopyingProtocol)

        return memScoped {
            val result = alloc<kotlinx.cinterop.COpaquePointerVar>()
            val status = SecItemCopyMatching(query as platform.CoreFoundation.CFDictionaryRef, result.ptr)
            if (status == 0 && result.value != null) {
                val data = result.value as? NSData ?: return@memScoped null
                NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
            } else {
                null
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun borrarToken() {
        val query = queryBase()
        SecItemDelete(query as platform.CoreFoundation.CFDictionaryRef)
    }
}
