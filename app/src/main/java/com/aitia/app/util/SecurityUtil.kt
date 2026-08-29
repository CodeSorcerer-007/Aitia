package com.aitia.app.util

import java.security.MessageDigest

object SecurityUtil {
    fun hashPin(pin: String): String {
        if (pin.isEmpty()) return ""
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
