package com.example.githubstars

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.githubstars.databinding.ActivityMainBinding
import com.example.githubstars.util.SensitiveValues
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    //TODO: remember to use  private val viewModel: UserProfileViewModel by viewModels() need "androidx.fragment:fragment-ktx:latest-version"
    //TODO: use val userId : String = savedStateHandle["uid"] ?:
    //          throw IllegalArgumentException("missing user id") if appropriate
    //https://developer.android.com/jetpack/docs/guide

    //TODO: use LiveData. Or Rx?



    //TODO: point out adaptive icon, in NY style
    // TODO: point out collapsing toolbar

    //TODO: ProGuard?

    //TODO: test on Lollipop

    //TODO: make sure my build has no warnings

    //TODO: make screen rotate and be resizeable
    //TODO: comment on Github capitalization in
    //TODO: MVVM
    //TODO: should the recyclerview use databinding instead of viewbinding?

    //TODO: clean up any unused files, functions or resources

    //TODO: empty view for RecyclerView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // To use View Binding or not to use View Binding? At a previous job, we used Kotlin synthetics.
        // This was convenient. We renamed our View IDs from snake_case to camelCase so they would be
        // camelCase in Kotlin. View Binding is slightly more verbose to use, but is safer,
        // and converts snake_case XML IDs to camelCase Kotlin property names. Nice! Also, it seems
        // Google suggests not using Kotlin synthetics. https://proandroiddev.com/the-argument-over-kotlin-synthetics-735305dd4ed0
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // We are only using this code for API level 26 (and not higher) because our v27/styles.xml
        // Takes over there. This only exists because SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR is
        // available in API level 26, but android:windowLightNavigationBar isn't available until
        // API level 27. Once the minimum SDK is 27, this code can be removed.
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            val decorView: View = window.decorView
            decorView.systemUiVisibility =
                    FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                    SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                    SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }



//        val encryptionKey = BuildConfig.ENCRYPTION_KEY
//        val encryptionKeyByteArray = Base64.decode(encryptionKey, Base64.DEFAULT)
//        val key = SecretKeySpec(encryptionKeyByteArray, 0, encryptionKeyByteArray.size, "AES")
//
//        val encryptedApiKey = BuildConfig.GITHUB_API_KEY[0]
//        val encryptedApiKeyBytes = Base64.decode(encryptedApiKey, Base64.DEFAULT)
//        val ivString = BuildConfig.GITHUB_API_KEY[1]
//        val ivBytes = Base64.decode(ivString, Base64.DEFAULT)
//        val ivSpec = IvParameterSpec(ivBytes)
//
//        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
//        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
//
//        val decodedBytes = cipher.doFinal(encryptedApiKeyBytes)
//        val decodedString = String(decodedBytes)

//        val decodedString = SensitiveValues.decrypt(BuildConfig.GITHUB_API_KEY)


        Log.e("xxx", "decoded API key: ${SensitiveValues.decrypt(BuildConfig.GITHUB_API_KEY)}")
        Log.e("xxx", "decoded XYZZY key : ${SensitiveValues.decrypt(BuildConfig.XYZZY)}")
        Log.e("xxx", "decoded API key 2: ${SensitiveValues.decrypt(BuildConfig.GITHUB_API_KEY)}")
        Log.e("xxx", "decoded PLUGH key : ${SensitiveValues.decrypt(BuildConfig.PLUGH)}")
        Log.e("xxx", "decoded API key 3: ${SensitiveValues.decrypt(BuildConfig.GITHUB_API_KEY)}")
        Log.e("xxx", "decoded PLOVER key : ${SensitiveValues.decrypt(BuildConfig.PLOVER)}")

        //TODO: temp
//        Log.e("xxx", "api key: ${BuildConfig.GITHUB_API_KEY[0]}")
    }

}
