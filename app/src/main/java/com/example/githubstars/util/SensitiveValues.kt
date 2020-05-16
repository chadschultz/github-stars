package com.example.githubstars.util

import android.util.Base64
import android.util.Log
import com.example.githubstars.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SensitiveValues {

    private val cache = mutableMapOf<String, String>()

    private val keySpec: SecretKeySpec by lazy {
        val encryptionKeyByteArray = bytesFrom(BuildConfig.ENCRYPTION_KEY)
        SecretKeySpec(encryptionKeyByteArray, 0, encryptionKeyByteArray.size, "AES")
    }

    fun decrypt(encrypted: Array<String>): String {
        //TODO: clean up
        val encryptedValue = encrypted[0]
        return cache[encryptedValue] ?: run {
            //TODO: temp
            Log.e("xxx", "decrypting...")
            //TODO: can/should I cache the key?
//            val encryptionKeyByteArray = bytesFrom(BuildConfig.ENCRYPTION_KEY)
//            val key = SecretKeySpec(encryptionKeyByteArray, 0, encryptionKeyByteArray.size, "AES")

            val ivSpec = IvParameterSpec(bytesFrom(encrypted[1]))
            //TODO: can I cache cipher? Should I?
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            val decodedBytes = cipher.doFinal(bytesFrom(encryptedValue))
            val decodedString = String(decodedBytes)

            cache[encryptedValue] = decodedString

            decodedString
        }

    }

    private fun bytesFrom(string: String) = Base64.decode(string, Base64.DEFAULT)
}