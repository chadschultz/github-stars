package com.example.githubstars

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.githubstars.databinding.ActivityMainBinding

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
    }

}
