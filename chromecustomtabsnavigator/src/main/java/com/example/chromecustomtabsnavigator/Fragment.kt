package com.example.chromecustomtabsnavigator

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

/**
 * Find a [ChromeCustomTabsNavigator] given a [Fragment]
 *
 * Calling this on a Fragment that is not a [NavHostFragment] or within a [NavHostFragment]
 * will result in an [IllegalStateException]
 *
 * This is based on Fragment.kt in androidx.navigation. This provides a convenient shortcut
 * to [ChromeCustomTabsNavigator].
 */
fun Fragment.findChromeCustomTabsNavigator(): ChromeCustomTabsNavigator =
    findNavController().navigatorProvider.getNavigator(ChromeCustomTabsNavigator::class.java)
