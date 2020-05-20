package com.example.githubstars.util

import android.util.Base64
import com.example.githubstars.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * This class is used to decrypt and access sensitive values (such as API keys) that were encrypted
 * and stored in [BuildConfig].
 * This method is not foolproof. Ultimately, your sensitive values and everything needed to decrypt
 * them are saved in the APK. Enabling code obfuscation for your signed builds is very important,
 * as this makes it more complicated for a hacker to dig the encryption key and encrypted values out
 * of the APK, but a determined hacker could still do it. You can minimize your risk with these tips:
 *  1. Is the sensitive value truly needed? Perhaps that API key is no longer used, or is needed for
 *      trivial functionality that can be omitted.
 *  2. In the case of API keys, is the scope as narrow as possible, with the fewest possible permissions,
 *      and taking advantage of any optional security restrictions offered? It may also be a good idea
 *      to use different API keys for different apps. If the API key is compromised, then you will only
 *      need to update one app, instead of multiple apps or platforms.
 *  3. Is it possible to have the API key stored server side, so your app communicates with your backend,
 *      which communicates with the third party service, instead of your app communicating directly with
 *      the third party service? This can be a hassle, but is more secure as API keys do not need to be
 *      stored in the app.
 * You might wonder why this matters. Who cares if someone else gets access to your API key?
 *  1. By using your key, they might use up your quota, if there is one, or rack up charges for a paid service.
 *  2. They might use your key to gain access to personal or confidential information that they can leak.
 *  3. They might use your API key to alter customer records or engage in other nefarious acts.
 *  4. They might expose how they accessed your API keys. This could lead to a perception of your app
 *      being unsafe or your company not taking proper security precautions, which would be bad PR.
 * Again, encrypting your API keys in the app is not a foolproof measure. But hopefully at this point
 * you've minimized the number of API keys in your app, and restricted those keys as much as possible.
 * Now what?
 * 1. Follow the instructions in the comments in sensitive-values.gradle to obfuscate your code,
 *  save your plaintext API keys (preferably not committed to your repository) and call the code
 *  in sensitive-values.gradle to encrypt your sensitive values and store them in [BuildConfig]
 * 2. When you need one of those values, call [decrypt] and pass in the identifier from [BuildConfig].
 *  This will match the key for the value in the properties file or the environment variable.
 *  The result is a plaintext String you can use.
 */
object SensitiveValues {

    private val cache = mutableMapOf<String, String>()

    private val keySpec: SecretKeySpec by lazy {
        val encryptionKeyByteArray = bytesFrom(BuildConfig.ENCRYPTION_KEY)
        SecretKeySpec(encryptionKeyByteArray, 0, encryptionKeyByteArray.size, "AES")
    }

    private val cipher: Cipher by lazy {
        Cipher.getInstance("AES/CBC/PKCS5PADDING")
    }

    /**
     * Convert an encrypted sensitive value from [BuildConfig] to plaintext for your use.
     * This caches decrypted values in memory, to avoid the overhead of decrypting on every reference.
     * See the instructions in `sensitive-values.gradle` for how to specify values to be encoded
     * during a Gradle build. You can also look in [BuildConfig] and see the encoded values
     * in the section marked with:
     * `// Fields from build type: debug`
     * (or another build type).
     * This section will also have [BuildConfig.ENCRYPTION_KEY]. This is automatically generated
     * during a Gradle build and referenced by this file. You do not need to interact with it directly
     * from your app code.
     * [encrypted] is an [Array] instead of a [String] because it contains two values.
     * `encrypted[0]` is the encrypted sensitive value. `encrypted[1]` is the IV (initialization
     * vector). This is automatically generated and is different for every encrypted value. It is required
     * for decryption.
     */
    fun decrypt(encrypted: Array<String>): String {
        val encryptedValue = encrypted[0]
        return cache[encryptedValue] ?: run {
            val ivSpec = IvParameterSpec(bytesFrom(encrypted[1]))
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decoded = String(cipher.doFinal(bytesFrom(encryptedValue)))
            cache[encryptedValue] = decoded
            decoded
        }
    }

    private fun bytesFrom(string: String) = Base64.decode(string, Base64.DEFAULT)
}
