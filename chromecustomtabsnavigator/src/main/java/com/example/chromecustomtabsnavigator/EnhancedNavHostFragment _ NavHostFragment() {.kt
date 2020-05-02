package com.example.chromecustomtabsnavigator

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign

/**
 * From https://proandroiddev.com/add-chrome-custom-tabs-to-the-android-navigation-component-75092ce20c6a
 */
class EnhancedNavHostFragment : NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)

        context?.let {
            navController.navigatorProvider += com.example.chromecustomtabsnavigator.ChromeCustomTabsNavigator(
                it
            )
        }
    }

}
